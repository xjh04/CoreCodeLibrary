package com.jxdx.corecodelibrary

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

open class BaseRepository {
//    suspend fun <T> requestResponse(requestCall: suspend () -> BaseResponse<T>?): T? {
//        val response = withContext(Dispatchers.IO) {
//            withTimeout(10 * 1000) {
//                requestCall()
//            }
//        } ?: return null
//
//        if (response.isFailed()) {
//            throw ApiException(response.errorCode, response.errorMsg)
//        }
//        return response.data
//    }

    fun <T> emitAndFlowOnIo(requestCall : T) : Flow<T> {
        return flow {
            emit(requestCall)
        }.flowOn(Dispatchers.IO)
    }
}