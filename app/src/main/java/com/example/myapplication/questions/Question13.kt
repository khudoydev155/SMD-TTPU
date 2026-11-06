package com.example.myapplication.questions

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    // 1. runBlocking
    val runBlockingResult: String = runBlocking {
        // 2. launch
        val launchResult = launch {
            delay(50)
        }
        println("launch returned: $launchResult") // JobImpl

        // 3. async
        val asyncResult = async {
            delay(50)
            "Result from async"
        }
        println("async returned: $asyncResult") // DeferredCoroutine

        // 4. coroutineScope
        val scopeResult: String = coroutineScope {
            delay(50)
            "Result from coroutineScope"
        }
        println("coroutineScope returned: $scopeResult")

        asyncResult.await()
    }

    println("runBlocking returned: $runBlockingResult")
}
