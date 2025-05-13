package com.jxdx.corecodelibrary.versoncontrol.manager

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.jxdx.corecodelibrary.versoncontrol.VersionUtil.getVersion
import com.jxdx.corecodelibrary.versoncontrol.bean.NetworkType
import com.jxdx.corecodelibrary.versoncontrol.bean.VersionUpdateTask
import com.jxdx.corecodelibrary.versoncontrol.callback.NetworkStateCallback
import com.jxdx.corecodelibrary.versoncontrol.receiver.NetworkReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ConcurrentLinkedDeque

class VersionManager private constructor(context: Context) {
    companion object {
        @Volatile
        private var instance: VersionManager? = null

        // 线程安全的单例获取方法
        fun getInstance(context: Context): VersionManager =
            instance ?: synchronized(this) {
                //applicationContext 为了防止内存泄漏
                instance ?: VersionManager(context.applicationContext).also {
                    instance = it
                }
            }
    }

    private val networkReceiver by lazy {
        NetworkReceiver.getInstance(context)
    }

    private val dataStoreManager by lazy {
        DataStoreManager.getInstance(context)
    }

    private val installManager by lazy {
        InstallManager.getInstance(context)
    }

    private val TAG = "VersionManager"

    // 确保只有一个正在被处理的版本更新任务
    var isHandling = false

    private val mVersionUpdateTasks by lazy {
        ConcurrentLinkedDeque<VersionUpdateTask>()
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var curNetworkType = NetworkType.UNKNOWN

    init {
        // 注册网络状态变化的监听器
        networkReceiver.addListener(object : NetworkStateCallback {
            override fun onNetworkChanged(
                networkType: NetworkType
            ) {
                Log.d(TAG, "*网络状态变化*: 类型: $networkType")
                curNetworkType = networkType
                // 完成之前遗留的任务
                formerVerification(context)
            }
        })
    }

    //请求接口之后调用：检查任务部分，负责发送任务
    fun checkVersion(
        context: Context,
        versionUpdateTask: VersionUpdateTask,
    ) {
        //在触发 isMonitoring() 的时候一定是已经处理好了前置校验的

        if (getVersion(context) < versionUpdateTask.latestVersion) {

            Log.d(TAG, "checkVersion: 有版本更新")

            mVersionUpdateTasks.addLast(versionUpdateTask)
            networkReceiver.startMonitoring()

            coroutineScope.launch {
                Log.d(TAG, "任务id：${versionUpdateTask.taskId} -- 添加到数据存储")
                dataStoreManager.addVersionUpdateTask(context, versionUpdateTask)
                handleVersionUpdateTask(versionUpdateTask, context)
            }

        }
    }

    // 每次启动调用：检察一下存储的任务
    fun checkVersionUpdateTasks(context: Context) {
        Log.d(TAG, "每次启动检察一下存储的任务")
        coroutineScope.launch {
            dataStoreManager.versionUpdateTasksFlow.collect {
                if (it.isNotEmpty()) {
                    networkReceiver.startMonitoring()
                    mVersionUpdateTasks.clear()
                    mVersionUpdateTasks.addAll(it)
                    Log.d(TAG, " 检查到${mVersionUpdateTasks.size} 个任务遗留")
                    checkSingleTask(context)
                    if (mVersionUpdateTasks.size > 0) {
                        val task = mVersionUpdateTasks.first
                        handleVersionUpdateTask(task, context)
                    }
                } else {
                    Log.d(TAG, "没有任务遗留")
                }
            }
        }
    }

    //网络变化时调用：是否有遗留的任务未完成，去完成未完成的任务
    private fun formerVerification(context: Context) {
        if (mVersionUpdateTasks.isNotEmpty()) {
            Log.d(TAG, " ${mVersionUpdateTasks.size} 个任务遗留")
            checkSingleTask(context)
            if (mVersionUpdateTasks.size > 0) {
                val task = mVersionUpdateTasks.first
                coroutineScope.launch {
                    handleVersionUpdateTask(task, context)
                }
            }
        } else {
            Log.d(TAG, "没有任务遗留")
            //没有任务遗留的时候关闭监听
            networkReceiver.stopMonitoring()
        }
    }

    // 拿到任务下载前调用：每次处理任务时都要确保任务队列中只有一个任务，如果有多个任务就只保留最后一个任务（最新的）
    private fun checkSingleTask(context: Context) {
        if (mVersionUpdateTasks.size > 1) {
            val lastTask = mVersionUpdateTasks.last
            mVersionUpdateTasks.clear()
            mVersionUpdateTasks.add(lastTask)
            coroutineScope.launch {
                dataStoreManager.saveVersionUpdateTasks(
                    context = context,
                    tasks = mVersionUpdateTasks.toList()
                )
            }
            Log.d(TAG, "checkSingleTask: 只保留最后一个新任务")
        }
    }

    //拿到任务后调用：下载部分
    private suspend fun handleVersionUpdateTask(task: VersionUpdateTask, context: Context) {
        if (!isHandling) {
            Log.d(TAG, "处理任务 ${task.taskId} ${task.latestVersion}当前版本：${getVersion(context)}")
            if(task.latestVersion <= getVersion(context)){
                Log.d(TAG, "当前版本已经是最新版本，无需下载")
                clearTasks(context)
                return
            }
            isHandling = true
            Log.d(TAG, "拿到任务: 处理任务 ${task.taskId} ${task.downloadUrl}")
            Log.d(TAG, "当前网络状态: $curNetworkType")
            if (curNetworkType == NetworkType.DISCONNECTED) {
                Log.d(TAG, "当前网络不可用，无法下载")
                isHandling = false
                return
            }

            if (task.isSilentDownload && curNetworkType == NetworkType.WIFI) {
                Log.d(TAG, "在WIFI情况下，开始静默下载")
                // 下载逻辑
                goToDownload(task, context)

            } else if (task.isSilentDownload) {
                Log.d(
                    TAG,
                    "静默下载，在WIFI断开的情况下，变为显示下载"
                )
                task.isSilentDownload = false
                dataStoreManager.updateIsSilentDownload(
                    context,
                    task.taskId,
                    false
                )
                //下载逻辑
                withContext(Dispatchers.Main) {
                    DialogQueueManager.instance.showRenewDialog(task)
                }
            } else {
                if (curNetworkType != NetworkType.DISCONNECTED) {
                    Log.d(
                        TAG,
                        "显示下载，在网络可用的状态下继续下载"
                    )
                    task.isSilentDownload = true
                    dataStoreManager.updateIsSilentDownload(
                        context,
                        task.taskId,
                        true
                    )
                    //下载逻辑
                    withContext(Dispatchers.Main) {
                        DialogQueueManager.instance.showRenewDialog(task)
                    }
                }
            }
        }
    }

    fun goToDownload(task: VersionUpdateTask, context: Context) {
        coroutineScope.launch {
            DownloadManager.getInstance(context).handelDownloadTask(task, context)
        }
    }

    // 这里主要处理安装结果回调的逻辑
    suspend fun tryInstallApk(
        apkFile: File,
        context: Context,
        task: VersionUpdateTask
    ) {
        try {
            // 启动安装并等待结果
            val result = installManager.installApkWithCoroutines(apkFile, context)

            result.onSuccess {
                // 安装成功
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "安装成功，应用已更新", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "安装成功，应用已更新")
                }
            }.onFailure { error ->
                // 安装失败
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "安装失败: ${error.message}", Toast.LENGTH_SHORT).show()
                }
                Log.d(TAG, "安装失败: ${error.message}")
                if(task.mandatoryUpdate == 1){
                    // 强制更新
                    withContext(Dispatchers.Main){
                        if (task.isSilentDownload){
                            DialogQueueManager.instance.showInstallDialog(apkFile, task)
                        }else{
                            DialogQueueManager.instance.showRenewDialog(task)
                        }

                    }
                }
                else if (error.message == "用户取消了安装"){
                    clearTasks(context)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "安装过程出错", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "安装过程出错: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            //删除任务
            clearTasks(context)
        }
    }

    fun clearTasks(context: Context) {
        mVersionUpdateTasks.clear()
        coroutineScope.launch {
            dataStoreManager.saveVersionUpdateTasks(
                context = context,
                tasks = mVersionUpdateTasks.toList()
            )
        }
    }
}