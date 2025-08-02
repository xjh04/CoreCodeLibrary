package com.jxdx.corecodelibrary.versoncontrol.manager

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.jxdx.corecodelibrary.versoncontrol.bean.DownloadTask
import com.jxdx.corecodelibrary.versoncontrol.bean.VersionUpdateTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.apache.http.conn.ssl.AllowAllHostnameVerifier
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.SocketException


/**
 * 单例类
 * 对DownloadTask进行下载
 * 对外只暴露handelDownloadTask()方法
 */
class DownloadManager private constructor(context: Context) {
    companion object {
        @Volatile
        private var instance: DownloadManager? = null

        fun getInstance(context: Context): DownloadManager =
            instance ?: synchronized(this) {
                instance ?: DownloadManager(context.applicationContext).also {
                    instance = it
                }
            }

        // 下载缓冲区大小和更新频率
        private const val BUFFER_SIZE = 8 * 1024 // 8KB
        private const val UP_DATA_FREQUENCY = 1000L // 1 second

        //下载的状态，每个Task各自持有
        const val STATE_DOWNLOADING = 0
        const val STATE_PAUSED = 1
        const val STATE_COMPLETED = 2
    }

    private val dataStoreManager by lazy {
        DataStoreManager.getInstance(context)
    }

    private val versionManager by lazy {
        VersionManager.getInstance(context)
    }

    private val downLoadNotificationManager by lazy {
        DownLoadNotificationManager.instance
    }

    suspend fun handelDownloadTask(task: VersionUpdateTask, context: Context) {

        withContext(Dispatchers.IO) {

            try {
                val downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    ?: throw IOException("无法获取下载目录")

                Log.d("DownloadManager", "APK 文件路径: ${downloadDir.absolutePath}")

                //尝试下载任务
                val client = OkHttpClient().newBuilder()
                    .hostnameVerifier(AllowAllHostnameVerifier())
                    .build()

                if (!downloadDir.exists()) downloadDir.mkdirs()

                val apkFile = File(downloadDir, "update_${task.downloadUrl.hashCode()}.apk")

                // 先检查任务状态
                when (task.state) {
                    STATE_PAUSED -> {
                        task.state = STATE_DOWNLOADING
                        dataStoreManager.updateState(context, task.taskId, STATE_DOWNLOADING)

                        Log.d("DownloadManager", "开始下载任务")
                    }

                    STATE_DOWNLOADING -> {
                        Log.d("DownloadManager", "继续下载任务")
                    }

                    STATE_COMPLETED -> {
                        Log.d("DownloadManager", "任务已完成，准备安装 APK")
                        // 直接安装 APK
                        versionManager.tryInstallApk(apkFile,context,task)
                        return@withContext
                    }
                }

                // 已下载字节数
                val downloadedBytes = apkFile.length()
                Log.d(
                    "DownloadManager", "已经下载好的大小：" + formatFileSize(
                        downloadedBytes
                    )
                )

                val request =
                    Request.Builder().url(task.downloadUrl)
                        .header("Range", "bytes=$downloadedBytes-")
                        .build()
                client.newCall(request).execute().use { response ->

                    val contentLength = response.body?.contentLength() ?: -1

                    // 正式开始写入文件（断点续传）
                    when (response.code) {
                        // 服务器支持断点续传 (206 Partial Content)
                        HttpURLConnection.HTTP_PARTIAL -> {
                            Log.d("DownloadManager", "支持断点续传")
                            writeFile(
                                response = response,
                                apkFile = apkFile,
                                context = context,
                                contentLength = contentLength,
                                ifAppend = true,
                                downloadedBytes = downloadedBytes,
                                task
                            )
                        }
                        // 服务器不支持断点续传 (200 OK)
                        HttpURLConnection.HTTP_OK -> {
                            Log.d("DownloadManager", "不支持断点续传")
                            writeFile(
                                response = response,
                                apkFile = apkFile,
                                context = context,
                                contentLength = contentLength,
                                ifAppend = false,
                                downloadedBytes = downloadedBytes,
                                task
                            )
                        }

                        else -> {
                            Log.d("DownloadManager", "未知响应码: ${response.code}")
                            task.state = STATE_PAUSED
                            dataStoreManager.updateState(context, task.taskId, STATE_PAUSED)
                            versionManager.clearTasks(context)
                            versionManager.isHandling = false
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "下载失败: ${response.code}",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                            return@withContext
                        }
                    }

                    task.state = STATE_COMPLETED
                    dataStoreManager.updateState(context, task.taskId, STATE_COMPLETED)

                    //下载完成调用
                    withContext(Dispatchers.Main) {
                        Log.d("DownloadManager", "下载完成，准备安装 APK")
                        if (task.isSilentDownload) {
                            //通知安装
                            DialogQueueManager.instance.showInstallDialog(apkFile,task)
                        }
                    }
                    if (!task.isSilentDownload){
                        downLoadNotificationManager.sendDownloadCompleteNotification(
                            context
                        )
                        versionManager.tryInstallApk(apkFile,context,task)
                    }

                }
            } catch (e: SocketException) {
                withContext(Dispatchers.Main) {
                    Log.d("DownloadManager", "下载暂停")
                    Toast.makeText(context, "下载暂停", Toast.LENGTH_SHORT).show()
                    versionManager.isHandling = false
                    task.state = STATE_PAUSED
                    dataStoreManager.updateState(context, task.taskId, STATE_PAUSED)
                    if (!task.isSilentDownload) {
                        downLoadNotificationManager.sendDownloadPauseNotification(context)
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Log.e("DownloadManager", "下载失败", e)
                    Toast.makeText(context, "下载失败: ${e.message}", Toast.LENGTH_SHORT).show()
                    versionManager.isHandling = false
                    versionManager.clearTasks(context)
                    if (!task.isSilentDownload) {
                        downLoadNotificationManager.sendDownloadFailedNotification(
                            context,
                            e.message
                        )
                    }
                }
            }
        }
    }

    private fun writeFile(
        response: Response,
        apkFile: File,
        context: Context,
        contentLength: Long,
        ifAppend: Boolean,
        downloadedBytes: Long = 0L,
        task: DownloadTask
    ) {
        try {
            response.body?.byteStream()?.use { inputStream ->
                Log.d("DownloadManager", "开始写入文件，模式：${if (ifAppend) "追加" else "新建"}")
                FileOutputStream(apkFile, ifAppend).use { outputStream ->
                    Log.d("DownloadManager", "开始传输数据")
                    copyStreamWithProgress(
                        inputStream = inputStream,
                        outputStream = outputStream,
                        // 计算实际需要传输的总字节数
                        totalBytes = if (ifAppend) contentLength + downloadedBytes else contentLength,
                        initialBytes = downloadedBytes, // 传递已下载字节数
                        context = context,
                        task
                    )
                }
            }
        } catch (e: SocketException) {
            Log.d("DownloadManager", "连接中断")
            throw e
        } catch (e: IOException) {
            Log.e("DownloadManager", "文件写入失败", e)
            throw e
        } catch (e: Exception) {
            Log.e("DownloadManager", "未知写入错误", e)
            throw IOException("未知写入错误", e)
        }
    }

    private fun copyStreamWithProgress(
        inputStream: InputStream,
        outputStream: OutputStream,
        totalBytes: Long, // 总字节数
        initialBytes: Long, // 已存在的字节数
        context: Context,
        task: DownloadTask
    ) {
        val buffer = ByteArray(BUFFER_SIZE)
        var bytesCopied = 0L
        var bytesRead: Int

        var lastUpdateTime = System.currentTimeMillis()
        var lastUpdateBytes = 0L

        while (inputStream.read(buffer).also { bytesRead = it } != -1) {

            outputStream.write(buffer, 0, bytesRead)
            bytesCopied += bytesRead

            // 进度更逻辑
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastUpdateTime >= UP_DATA_FREQUENCY) {
                // 计算速度
                val deltaTime = currentTime - lastUpdateTime
                val deltaBytes = bytesCopied - lastUpdateBytes
                val speedBps = if (deltaTime > 0) (deltaBytes * 1000) / deltaTime else 0L

                if (!task.isSilentDownload) {
                    downLoadNotificationManager.updateProgressNotification(
                        context = context,
                        downloadedBytes = initialBytes + bytesCopied, // 累加已有字节
                        totalBytes = totalBytes,
                        speedBps = speedBps
                    )
                }

                lastUpdateTime = currentTime
                lastUpdateBytes = bytesCopied
            }
        }
        if (!task.isSilentDownload) {
            // 最终强制更新一次进度
            downLoadNotificationManager.updateProgressNotification(
                context = context,
                downloadedBytes = initialBytes + bytesCopied,
                totalBytes = totalBytes,
                speedBps = 0
            )
        }
    }

    private fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        var size = bytes.toDouble()
        var unitIndex = 0

        while (size >= 1024 && unitIndex < units.lastIndex) {
            size /= 1024
            unitIndex++
        }
        return "%.1f %s".format(size, units[unitIndex])
    }
}