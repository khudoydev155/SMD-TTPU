package com.example.myapplication.questions

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    // Launch version: returns Job, cannot give calculation result
    val job = launch {
        delay(100)
        val result = 6 * 7
        // job has no way to return `result` to caller!
    }
    println("launch returned Job object: $job (Cannot extract calculation result!)")

    // Async version: returns Deferred<T>, allows obtaining result via .await()
    val deferred = async {
        delay(100)
        6 * 7
    }
    val value = deferred.await()
    println("async returned Deferred<Int>, await() produced result: $value")
}
