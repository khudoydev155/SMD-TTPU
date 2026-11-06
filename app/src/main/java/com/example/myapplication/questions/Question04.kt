package com.example.myapplication.questions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

suspend fun loadUserData(): String {
    delay(100)
    return "User Data"
}

// Reproduction of compile error:
// fun normalFunction() {
//     val user = loadUserData() // ERROR: Suspend function 'loadUserData' should be called only from a coroutine or another suspend function
// }

// Fix 1: Make the caller a suspend function
suspend fun fix1Caller() {
    val user = loadUserData()
    println("Fix 1 loaded: $user")
}

// Fix 2: Launch a coroutine using a CoroutineScope or runBlocking
fun fix2Caller() {
    // Way 2a: runBlocking (Blocks current thread - NEVER use on Android UI thread)
    runBlocking {
        val user = loadUserData()
        println("Fix 2 (runBlocking) loaded: $user")
    }

    // Way 2b: CoroutineScope.launch (Non-blocking background launch)
    CoroutineScope(Dispatchers.Default).launch {
        val user = loadUserData()
        println("Fix 2 (launch) loaded: $user")
    }
}

fun main() = runBlocking {
    fix1Caller()
    fix2Caller()
    delay(200) // allow async launch in fix2 to finish
}
