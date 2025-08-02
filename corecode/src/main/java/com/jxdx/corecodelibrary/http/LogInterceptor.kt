package com.jxdx.corecodelibrary.http

import com.google.gson.Gson
import com.jxdx.corecodelibrary.util.TimeUtils
import com.orhanobut.logger.Logger
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.nio.charset.Charset

class LogInterceptor : Interceptor {
    // 懒加载 UTF-8 字符集
    private val charsetUtf8: Charset by lazy {
        Charset.forName("UTF-8")
    }

    // 拦截 HTTP 请求和响应
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // 生成并记录请求详情
        generateRequestLog(request).let {
            Logger.json(it)
        }

        // 继续处理请求并获取响应
        val response = chain.proceed(request)

        // 获取并记录响应详情
        getResponseText(response)?.let {
            Logger.json(it)
        }

        // 返回响应
        return response
    }

    // 生成 HTTP 请求的 JSON 日志
    private fun generateRequestLog(request: Request): String = Gson().toJson(
        HttpRequest(
            TimeUtils.millis2String(System.currentTimeMillis()), // 当前时间的字符串格式
            request.url.toString(), // 请求 URL
            request.method, // HTTP 方法 (GET, POST 等)
            getRequestParams(request) // 请求参数
        )
    )

    /**
     * 获取请求参数
     */
    private fun getRequestParams(request: Request): MutableMap<String, String> {
        val result: MutableMap<String, String> = mutableMapOf()
        var str = ""
        try {
            request.body?.let {
                val buffer = Buffer()
                it.writeTo(buffer)
                val charset = it.contentType()?.charset(charsetUtf8) ?: charsetUtf8
                str = buffer.readString(charset)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (str.isNotEmpty()) {
            val array = str.split("&")
            for (params in array) {
                val array2 = params.split("=")
                if (array2.size == 2) {
                    result[array2[0]] = array2[1]
                } else {
                    result["body"] = array2[0]
                }
            }
        }
        return result
    }

    /**
     * 获取返回数据字符串
     */
    private fun getResponseText(response: Response): String? {
        try {
            response.body?.let {
                val source = it.source()
                source.request(Long.MAX_VALUE)
                val buffer = source.buffer
                val charset = it.contentType()?.charset(charsetUtf8) ?: charsetUtf8
                if (it.contentLength().toInt() != 0) {
                    buffer.clone().readString(charset).let { result ->
                        return result
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
