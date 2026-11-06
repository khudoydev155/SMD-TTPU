package com.example.myapplication.questions

import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val parentJob = launch {
        launch {
            try {
                delay(1000)
            } finally {
                println("Request 1 cleanup executed")
            }
        }
        launch {
            try {
                delay(1000)
            } finally {
                println("Request 2 cleanup executed")
            }
        }
    }

    delay(100)
    parentJob.cancelAndJoin()
    println("end")
}
