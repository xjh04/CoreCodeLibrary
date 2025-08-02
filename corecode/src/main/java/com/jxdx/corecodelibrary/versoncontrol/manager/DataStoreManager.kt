package com.jxdx.corecodelibrary.versoncontrol.manager

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.jxdx.corecodelibrary.versoncontrol.bean.VersionUpdateTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreManager private constructor(context: Context) {

    companion object {
        @Volatile
        private var instance: DataStoreManager? = null

        // 线程安全的单例获取方法
        fun getInstance(context: Context): DataStoreManager =
            instance ?: synchronized(this) {
                instance ?: DataStoreManager(context.applicationContext).also {
                    instance = it
                }
            }
    }

    private val gson = Gson()
    private val versionUpdateTasksKey = stringPreferencesKey("VersionUpdateTasks") // 键名改为复数
    private val Context.versionUpdateTaskDataStore by preferencesDataStore(name = "VersionUpdateTasks")

    // 保存任务列表（全量覆盖）
    // 保存方法返回操作结果
    suspend fun saveVersionUpdateTasks(
        context: Context,
        tasks: List<VersionUpdateTask>
    ): Boolean {
        return try {
            context.versionUpdateTaskDataStore.edit { prefs ->
                prefs[versionUpdateTasksKey] = gson.toJson(tasks)
            }
            true
        } catch (e: Exception) {
            Log.d("DataStoreManager", "保存任务列表失败: ${e.message}")
            false
        }
    }

    // 暴露一个不可变的 Flow 供外部观察数据集合
    //Flow 每次发射的是 一个完整的列表对象，列表中可能包含多个任务实例。
    val versionUpdateTasksFlow: Flow<List<VersionUpdateTask>> =
        // 从 DataStore 获取原始 Preferences 数据流
        context.versionUpdateTaskDataStore.data
            // 异常捕获处理（处理上游数据流可能抛出的异常）
            .catch { ex ->
                // 如果是 IO 异常（如文件读取失败）
                if (ex is IOException) {
                    // 发射空 Preferences 对象保持流程继续
                    emit(emptyPreferences())
                } else {
                    // 非 IO 异常重新抛出（如类型转换错误，由上层统一处理）
                    throw ex
                }
            }
            // 将 Preferences 转换为业务对象列表
            .map { prefs ->
                try {
                    // 尝试从 Preferences 获取存储的 JSON 字符串
                    prefs[versionUpdateTasksKey]?.let { json ->
                        // 使用 Gson 进行反序列化时需要处理泛型擦除问题
                        val type = object : TypeToken<List<VersionUpdateTask>>() {}.type

                        // 执行反序列化操作（可能抛出 JsonSyntaxException）
                        gson.fromJson<List<VersionUpdateTask>>(json, type)
                        // 处理空指针保护（当 json 是 "null" 时返回空列表）
                            ?: emptyList()
                    }
                    // 如果键值不存在（首次使用或数据被清空），返回空列表
                        ?: emptyList()
                } catch (e: JsonSyntaxException) {
                    // JSON 格式错误处理（如手动修改存储文件导致格式损坏）
                    emptyList() // 安全降级，返回空列表避免崩溃
                    // 建议在此添加日志记录：Log.e("DataStore", "JSON 解析失败", e)
                }
            }

    // 添加单个任务（需要先读取现有列表）
    suspend fun addVersionUpdateTask(context: Context, task: VersionUpdateTask) {
        // 使用 first() 获取当前列表，并查找指定任务
        val currentTasks = versionUpdateTasksFlow.first().toMutableList()
        currentTasks.add(task)
        saveVersionUpdateTasks(context, currentTasks)
        Log.d("DataStoreManager", "添加任务成功: ${task.taskId}")
    }

    // 移除单个任务（假设任务有唯一标识符如 id）
    suspend fun removeVersionUpdateTaskById(context: Context, taskId: Long) {
        val currentTasks = versionUpdateTasksFlow.first()
            .filterNot { it.taskId == taskId }
        saveVersionUpdateTasks(context, currentTasks)
        Log.d("DataStoreManager", "移除任务成功: $taskId")
    }

    // 修改 isSilentDownload 字段
    suspend fun updateIsSilentDownload(context: Context, taskId: Long, newValue: Boolean): Boolean {
        val currentTasks = versionUpdateTasksFlow.first().toMutableList()
        val task = currentTasks.find { it.taskId == taskId } ?: return false
        task.isSilentDownload = newValue
        Log.d("DataStoreManager", "修改任务成功: $taskId")
        return saveVersionUpdateTasks(context, currentTasks)
    }

    // 修改 state 字段
    suspend fun updateState(context: Context, taskId: Long, newState: Int): Boolean {
        val currentTasks = versionUpdateTasksFlow.first().toMutableList()
        val task = currentTasks.find { it.taskId == taskId } ?: return false
        task.state = newState
        Log.d("DataStoreManager", "修改任务状态成功: $taskId")
        return saveVersionUpdateTasks(context, currentTasks)
    }

}