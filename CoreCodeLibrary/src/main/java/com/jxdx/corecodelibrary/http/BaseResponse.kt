package com.jxdx.corecodelibrary.http

class BaseResponse<T>(
    val code: Int? = null,
    val message: String? = null,
    val data: T? = null,
)
