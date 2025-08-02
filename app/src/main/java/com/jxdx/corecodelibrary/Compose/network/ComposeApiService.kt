package com.jxdx.corecodelibrary.Compose.network

import retrofit2.http.GET
import retrofit2.http.Query


interface ComposeApiService {
    @GET("api/user/info")
    suspend fun getUserInfo(@Query("userId") userId: String): NetworkUser
}
// 服务器返回的用户信息 JSON 对应的数据类
data class NetworkUser(
    val name: String,
    val userId: String,
    val device: String,
    val osVersion: String
    // oaid 可能来自 SDK，而不是服务器
)