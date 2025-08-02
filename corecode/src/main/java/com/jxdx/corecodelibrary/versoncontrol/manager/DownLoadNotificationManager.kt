package com.jxdx.corecodelibrary.versoncontrol.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

/**
 * 通知控制类 - 负责管理版本更新过程中的各种通知状态
 */
class DownLoadNotificationManager private constructor() {
    // 通知管理器实例
    private var notificationManager: NotificationManager? = null

    companion object {
        // 单例实例
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            DownLoadNotificationManager()
        }
        // 通知渠道配置常量
        private const val CHANNEL_ID = "download_channel"         // 通知渠道ID
        private const val NOTIFICATION_ID = 1                    // 统一通知ID
    }

    /**
     * 创建通知渠道
     * @param context 上下文对象
     */
    fun createNotificationChannel(context: Context) {
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        // 如果渠道不存在则创建
        if (notificationManager?.getNotificationChannel(CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "应用下载",  // 渠道名称
                NotificationManager.IMPORTANCE_LOW  // 低重要性（不弹出通知，显示在状态栏）
            ).apply {
                description = "用于显示版本更新下载进度"  // 渠道描述
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun destroyNotificationChannel() {
        notificationManager?.deleteNotificationChannel(CHANNEL_ID)
    }

    /**
     * 更新下载进度通知
     * @param context 上下文对象
     * @param downloadedBytes 已下载字节数
     * @param totalBytes 总字节数
     * @param speedBps 当前下载速度（字节/秒）
     */
    fun updateProgressNotification(
        context: Context,
        downloadedBytes: Long,
        totalBytes: Long,
        speedBps: Long
    ) {
        // 计算下载百分比（当总大小未知时返回-1）
        val progress = when {
            totalBytes > 0 -> ((downloadedBytes.toDouble() / totalBytes) * 100).toInt()
            else -> -1
        }

        // 格式化下载速度显示
        val speedText = when {
            speedBps >= 1024 * 1024 -> "%.1f MB/s".format(speedBps / (1024.0 * 1024))
            speedBps >= 1024 -> "%.1f KB/s".format(speedBps / 1024.0)
            else -> "$speedBps B/s"
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)  // 系统下载图标
            .setPriority(NotificationCompat.PRIORITY_LOW)       // 低优先级
            .setOnlyAlertOnce(true)  // 仅第一次通知时提醒

        when {
            // 已知总大小的进度显示
            progress >= 0 -> {
                builder.setContentTitle("下载进度: $progress%")
                    .setContentText(
                        "已下载 ${formatFileSize(downloadedBytes)}/${
                            formatFileSize(totalBytes)
                        } • $speedText"
                    )
                    .setProgress(100, progress, false)  // 确定型进度条
            }
            // 未知总大小的进度显示
            else -> {
                builder.setContentTitle("正在下载")
                    .setContentText("已下载 ${formatFileSize(downloadedBytes)} • $speedText")
                    .setProgress(100, -1, true)  // 不确定型进度条
            }
        }

        notificationManager?.notify(NOTIFICATION_ID, builder.build())
    }

    /**
     * 发送下载完成通知
     * @param context 上下文对象
     * @param apkFile 下载完成的APK文件
     */
    fun sendDownloadCompleteNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)  // 下载完成图标
            .setContentTitle("下载完成")

            .setPriority(NotificationCompat.PRIORITY_HIGH)  // 高优先级

        notificationManager?.notify(NOTIFICATION_ID, builder.build())
    }

    /**
     * 发送下载失败通知
     * @param context 上下文对象
     * @param errorMessage 错误信息（可选）
     */
    fun sendDownloadFailedNotification(context: Context, errorMessage: String?) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_error)  // 错误图标
            .setContentTitle("下载失败")
            .setContentText(errorMessage ?: "未知错误")  // 显示具体错误信息
            .setPriority(NotificationCompat.PRIORITY_HIGH)  // 高优先级

        notificationManager?.notify(NOTIFICATION_ID, builder.build())
    }

    /**
     * 发送下载暂停通知（带继续操作按钮）
     * @param context 上下文对象
     */
    fun sendDownloadPauseNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("下载已暂停")
            .setContentText("网络不可用")

            .setPriority(NotificationCompat.PRIORITY_LOW)  // 低优先级

        notificationManager?.notify(NOTIFICATION_ID, builder.build())
    }

    /**
     * 格式化文件大小（私有工具方法）
     * @param bytes 字节数
     * @return 格式化后的字符串
     */
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