package com.jxdx.corecodelibrary.http

class HttpRequest(
    val requestTime: String,
    val requestUrl: String,
    val requestMethod: String,
    val requestParams: MutableMap<String, String>
)