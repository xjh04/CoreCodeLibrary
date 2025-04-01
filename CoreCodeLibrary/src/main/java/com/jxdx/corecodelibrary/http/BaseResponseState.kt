package com.jxdx.corecodelibrary.http

sealed class BaseResponseState<T> {
    // 成功状态，携带数据
    data class Success<T>(val data: T) : BaseResponseState<T>()

    // 失败状态，携带错误信息
    data class Error<T>(val message: String, val code: Int = -1) : BaseResponseState<T>()

    // 加载中状态
    class Loading<T> : BaseResponseState<T>()
}
