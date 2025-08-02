package com.jxdx.corecodelibrary.http

import android.net.ParseException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class ResponseInterceptor : Interceptor {
    private val gson = Gson()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        // 复制原始响应体内容
        val responseBody = response.body?.string() ?: return response

        if (response.isSuccessful) {
            // 仅解析 code 字段
            val code = try {
                val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)
                jsonObject.get("code").asInt
            } catch (e: Exception) {
                // 解析失败时抛出异常
                throw ParseException("Failed to parse code: ${e.message}")
            }

            when (code) {
                0 -> {
                    Log.d("ResponseInterceptor", "code = 1 : 通用成功")
                }
                101 -> {
                    Log.d("ResponseInterceptor", "code = 101 : 未登录")
                }
                102 -> {
                    Log.d("ResponseInterceptor", "code = 102 : 账号被踢出")
                }
                103 -> {
                    Log.d("ResponseInterceptor", "code = 103 : 密码已修改")
                }
                104 -> {
                    Log.d("ResponseInterceptor", "code = 104 : 失效Token")
                }
                else -> Log.d("ResponseInterceptor", "通用错误")
            }
        }
        // 重新构建新的 Response 对象，避免 closed 异常
        val contentType = response.body?.contentType()
        val newBody = responseBody.toResponseBody(contentType)
        return response.newBuilder().body(newBody).build()
    }
}