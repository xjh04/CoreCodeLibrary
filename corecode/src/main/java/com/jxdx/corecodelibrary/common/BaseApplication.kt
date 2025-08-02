package com.jxdx.corecodelibrary.common

import android.app.Application
import com.jxdx.corecodelibrary.http.HttpManager
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy


class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true) // （可选）是否显示线程信息。默认值为 true
            .tag("coreCode") // （可选）每个日志的全局标签。默认值为 PRETTY_LOGGER
            .methodCount(5)
            .build()

        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))


        HttpManager.init("http://47.99.43.189:8898/")
    }
}