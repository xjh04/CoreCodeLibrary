package com.jxdx.corecodelibrary.util

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtils {
    private val SDF_THREAD_LOCAL: ThreadLocal<MutableMap<String, SimpleDateFormat>> =
        object : ThreadLocal<MutableMap<String, SimpleDateFormat>>() {
            override fun initialValue(): MutableMap<String, SimpleDateFormat> {
                return HashMap(16)
            }
        }
    private val defaultFormat: SimpleDateFormat
        get() = getSafeDateFormat("yyyy-MM-dd HH:mm:ss")

    @SuppressLint("SimpleDateFormat")
    fun getSafeDateFormat(pattern: String): SimpleDateFormat {
        val sdfMap: MutableMap<String, SimpleDateFormat>? = SDF_THREAD_LOCAL.get()
        var simpleDateFormat = sdfMap?.get(pattern)
        if (simpleDateFormat == null) {
            simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
            sdfMap?.put(pattern, simpleDateFormat)
        }
        return simpleDateFormat
    }

    /**
     * 毫秒数转换为格式化时间字符串。
     *
     * @param millis 毫秒数。
     * @param format 格式。
     * @return 格式化时间字符串
     */
    @JvmOverloads
    fun millis2String(millis: Long, format: DateFormat = defaultFormat): String {
        return format.format(Date(millis))
    }

    /**
     * 毫秒数转换为格式化时间字符串。
     *
     * @param millis  毫秒数。
     * @param pattern 日期格式的模式，例如 yyyy/MM/dd HH:mm
     * @return 格式化时间字符串
     */
    fun millis2String(millis: Long, pattern: String): String {
        return millis2String(millis, getSafeDateFormat(pattern))
    }
}
