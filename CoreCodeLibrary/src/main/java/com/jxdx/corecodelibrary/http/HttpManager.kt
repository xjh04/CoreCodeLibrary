package com.jxdx.corecodelibrary.http

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class HttpManager(baseUrl: String) {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(LogInterceptor())
            .addInterceptor(ResponseInterceptor())
            .build()
    }

    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
        .client(okHttpClient)
        .build()

    fun <Service>createRetrofit(service: Class<Service>): Service {
        return retrofit.create(service)
    }


    companion object Instance {
        lateinit var instance: HttpManager
            private set

        fun init(baseUrl: String) {
            instance = HttpManager(baseUrl)
        }
    }
}