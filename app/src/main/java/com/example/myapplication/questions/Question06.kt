package com.example.myapplication.questions

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val threadName = Thread.currentThread().name
    for (i in 1..10) {
        launch {
            println("download $i started on thread $threadName")
            delay(500)
            println("download $i finished on thread $threadName")
        }
    }
}
