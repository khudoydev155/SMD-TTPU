package com.example.myapplication.questions

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

suspend fun getCurrentThreadName(): String {
    delay(10) // explicit suspend point
    return Thread.currentThread().name
}

fun main() = runBlocking {
    val mainThreadName = Thread.currentThread().name
    println("Thread name before call: $mainThreadName")

    val threadInsideSuspend = getCurrentThreadName()
    println("Thread name inside suspend function: $threadInsideSuspend")

    if (mainThreadName == threadInsideSuspend) {
        println("Disproved belief: marking a function 'suspend' does NOT change the execution thread!")
    } else {
        println("Proved belief: thread changed.")
    }
}
