package com.jxdx.corecodelibrary

import com.jxdx.corecodelibrary.http.BaseResponse
import retrofit2.http.GET

interface ApiService {
    @GET("user/isLogin")
    suspend fun isLogin(): BaseResponse<String>
}