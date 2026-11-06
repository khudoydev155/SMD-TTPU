package com.example.myapplication.questions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// Helper 1: Starts greeter in background and returns immediately
fun CoroutineScope.startBackgroundGreeter(name: String): Job {
    return launch {
        delay(100)
        println("Background Greeter: Hello $name")
    }
}

// Helper 2: Greets several people and returns only when all greetings are printed
suspend fun greetAllSequentially(names: List<String>) = coroutineScope {
    for (name in names) {
        launch {
            delay(100)
            println("Sequential Greeter: Hello $name")
        }
    }
}

fun main() = runBlocking {
    println("--- Calling Helper 1 (CoroutineScope Extension) ---")
    startBackgroundGreeter("Alice")
    println("Line right after Helper 1 call (Returns immediately!)")

    delay(200) // Wait for background greeter output

    println("\n--- Calling Helper 2 (Suspend function with coroutineScope) ---")
    greetAllSequentially(listOf("Bob", "Charlie"))
    println("Line right after Helper 2 call (Waits until all done!)")
}
