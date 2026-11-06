package com.example.myapplication.questions

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    println("Start")
    coroutineScope {
        // Export X: 300 ms
        launch {
            delay(300)
            println("X")
        }
        // Export Y: 100 ms
        launch {
            delay(100)
            println("Y")
        }
        // Export Z: 200 ms
        launch {
            delay(200)
            println("Z")
        }
    }
    println("All done")
}
