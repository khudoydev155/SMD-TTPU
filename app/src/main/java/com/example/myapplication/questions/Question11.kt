package com.example.myapplication.questions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun CoroutineScope.startCounter(name: String, maxCount: Int): Job {
    return launch {
        for (i in 1..maxCount) {
            delay(100)
            println("$name: $i")
        }
    }
}

fun main() = runBlocking {
    coroutineScope {
        startCounter("Counter A", 3)
        startCounter("Counter B", 2)
        println("launched")
    }
}
