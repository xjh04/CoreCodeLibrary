package com.jxdx.corecodelibrary

inline fun runSomething(param: String, run: () -> Unit) {
    println("param:$param")
    run.invoke()
}

fun main() {
    runSomething("Hello World!") {
        println("runSomething")
    }
}
