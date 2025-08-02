package com.jxdx.corecodelibrary.versoncontrol
import android.content.Context

object VersionUtil {
    fun getVersion(context: Context): String {
        return try {
            context.packageManager
                .getPackageInfo(context.packageName, 0)
                .versionName
        } catch (e: Exception) {
            "Unknown" // 异常处理
        }
    }
}