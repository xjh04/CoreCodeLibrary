package com.jxdx.corecodelibrary.versoncontrol.manager

import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInstaller
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class InstallManager private constructor(context: Context) {
    companion object {
        @Volatile
        private var instance: InstallManager? = null

        // 线程安全的单例获取方法
        fun getInstance(context: Context): InstallManager =
            instance ?: synchronized(this) {
                //applicationContext 为了防止内存泄漏
                instance ?: InstallManager(context.applicationContext).also {
                    instance = it
                }
            }
    }

    private val TAG = "com.jxdx.corecodelibrary.versoncontrol.manager.InstallManager"
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    suspend fun installApkWithCoroutines(
        apkFile: File,
        context: Context
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            suspendCoroutine { continuation ->

                // 注册广播接收器来监听安装结果
                var receiver: BroadcastReceiver? = null

                // 添加安装状态追踪变量
                val installationHandled = AtomicBoolean(false)

                // 创建一个协程作用域用于超时处理
                val timeoutJob = launch {
                    delay(5 * 60 * 1000L) // 5分钟超时

                    if (installationHandled.compareAndSet(false, true)) {
                        Log.d(TAG, "安装超时，可能已被用户取消")

                        receiver?.let {
                            try {
                                context.unregisterReceiver(it)
                                receiver = null
                            } catch (e: Exception) {
                                Log.e(TAG, "解除注册安装接收器失败", e)
                            }
                        }

                        continuation.resume(Result.failure(Exception("安装超时，可能已被用户取消")))
                    }
                }

                // 注册PackageInstaller的Session回调
                val packageInstaller = context.packageManager.packageInstaller
                // 创建SessionCallback以接收取消安装的回调
                val sessionCallback = object : PackageInstaller.SessionCallback() {
                    override fun onCreated(sessionId: Int) {
                    }

                    override fun onBadgingChanged(sessionId: Int) {
                    }

                    override fun onActiveChanged(sessionId: Int, active: Boolean) {
                    }

                    override fun onProgressChanged(sessionId: Int, progress: Float) {
                    }

                    override fun onFinished(sessionId: Int, success: Boolean) {
                        Log.d(TAG, "安装会话结束: $sessionId, success: $success")
                        if (!success && installationHandled.compareAndSet(false, true)) {
                            timeoutJob.cancel()

                            try {
                                packageInstaller.unregisterSessionCallback(this)
                            } catch (e: Exception) {
                                Log.e(TAG, "解除注册会话回调失败", e)
                            }

                            try {
                                receiver?.let {
                                    context.unregisterReceiver(it)
                                    receiver = null
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "解除注册安装接收器失败", e)
                            }

                            continuation.resume(Result.failure(Exception("用户取消了安装")))
                        }
                    }
                }

                // 注册会话回调
                coroutineScope.launch(Dispatchers.Main) {
                    try {
                        packageInstaller.registerSessionCallback(sessionCallback)
                    } catch (e: Exception) {
                        Log.e(TAG, "注册会话回调失败", e)
                    }
                }

                // 创建广播接收器 - 处理所有事件
                receiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        if (intent == null) return
                        val action = intent.action ?: return

                        Log.d(TAG, "收到广播: $action")

                        when {
                            // 处理安装完成回调事件 (检查是否失败)
                            action == "android.content.pm.action.SESSION_COMPLETED" ||
                                    action == "com.your.package.INSTALL_COMPLETE" -> {
                                val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -1)
                                val sessionId = intent.getIntExtra(PackageInstaller.EXTRA_SESSION_ID, -1)

                                Log.d(TAG, "安装会话完成: sessionId=$sessionId, status=$status")

                                if (status != PackageInstaller.STATUS_SUCCESS &&
                                    installationHandled.compareAndSet(false, true)
                                ) {
                                    timeoutJob.cancel()

                                    val errorMessage =
                                        intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
                                            ?: "包冲突或其他安装错误"

                                    Log.e(
                                        TAG,
                                        "PackageInstaller安装失败: $errorMessage | 状态码: $status"
                                    )

                                    // 解除注册会话回调
                                    try {
                                        packageInstaller.unregisterSessionCallback(sessionCallback)
                                    } catch (e: Exception) {
                                        Log.e(TAG, "解除注册会话回调失败", e)
                                    }

                                    try {
                                        context?.unregisterReceiver(this)
                                        receiver = null
                                    } catch (e: Exception) {
                                        Log.e(TAG, "解除注册接收器失败", e)
                                    }

                                    continuation.resume(Result.failure(Exception(errorMessage)))
                                }
                            }
                        }
                    }
                }

                // 为合并的接收器创建广播过滤器
                val filter = IntentFilter().apply {
                    // 成功安装的事件
                    addAction(Intent.ACTION_PACKAGE_ADDED)
                    addAction(Intent.ACTION_PACKAGE_REPLACED)
                    // 失败可能相关的事件
                    addAction(Intent.ACTION_PACKAGE_REMOVED)
                    addAction("android.content.pm.action.SESSION_COMPLETED")
                    addAction("android.content.pm.action.SESSION_ABANDONED") // 添加会话放弃事件
                    addAction("com.your.package.INSTALL_COMPLETE")
                    addDataScheme("package")
                }

                // 在主线程中注册回调和广播接收器
                coroutineScope.launch {
                    context.registerReceiver(receiver, filter)
                }

                // 启动安装流程
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        apkFile
                    )
                    setDataAndType(uri, "application/vnd.android.package-archive")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                if (!apkFile.exists() || !apkFile.canRead()) {
                    if (installationHandled.compareAndSet(false, true)) {
                        timeoutJob.cancel()

                        // 解除注册会话回调
                        try {
                            packageInstaller.unregisterSessionCallback(sessionCallback)
                        } catch (e: Exception) {
                            Log.e(TAG, "解除注册会话回调失败", e)
                        }

                        continuation.resume(Result.failure(IOException("APK文件不可访问: ${apkFile.absolutePath}")))
                    }
                    return@suspendCoroutine
                }

                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    if (installationHandled.compareAndSet(false, true)) {
                        timeoutJob.cancel()

                        // 解除注册会话回调
                        try {
                            packageInstaller.unregisterSessionCallback(sessionCallback)
                        } catch (e: Exception) {
                            Log.e(TAG, "解除注册会话回调失败", e)
                        }

                        val errorMsg = when (e) {
                            is SecurityException -> "缺少安装权限"
                            is ActivityNotFoundException -> "未找到安装程序"
                            else -> e.message ?: "未知错误"
                        }

                        Log.e(TAG, "安装失败: $errorMsg", e)

                        // 解除所有注册
                        try {
                            receiver?.let {
                                context.unregisterReceiver(it)
                                receiver = null
                            }
                        } catch (ex: Exception) {
                            Log.e(TAG, "解除注册安装接收器失败", ex)
                        }

                        continuation.resume(Result.failure(Exception(errorMsg)))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "安装过程中发生异常", e)
            Result.failure(e)
        }
    }
}