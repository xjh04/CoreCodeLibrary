package com.jxdx.corecodelibrary

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun createCoroutine() {
    val scope = CoroutineScope(Dispatchers.Main)
    scope.launch {
        getResult()
        Dispatchers.Main
    }
}

suspend fun getResult(): String {
    withContext(Dispatchers.IO) {
        delay(1000)
    }
    return "result"
}