package com.jxdx.corecodelibrary

import com.jxdx.corecodelibrary.http.BaseResponse
import com.jxdx.corecodelibrary.http.BaseResponseState
import com.jxdx.corecodelibrary.http.HttpManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class MyRepository {

    private val service = HttpManager.instance.createRetrofit(ApiService::class.java)

    fun isLogin(): Flow<BaseResponseState<BaseResponse<String>>> = flow {
        emit(BaseResponseState.Loading())
        // 发起网络请求
        val isLogin = service.isLogin()
        // 发射成功状态
        emit(BaseResponseState.Success(isLogin))
    }.flowOn(Dispatchers.IO).catch {
        // 发射失败状态
        emit(BaseResponseState.Error(it.message ?: "未知的网络请求失败"))
    }
}