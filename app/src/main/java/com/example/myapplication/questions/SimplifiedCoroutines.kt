package com.example.myapplication.questions

import kotlinx.coroutines.*

// Question 1: First coroutine
fun q1() = runBlocking {
    launch {
        delay(1000)
        println("Coffee ready")
    }
    println("Order accepted")
}

// Question 2: Three baristas
fun q2Delay() = runBlocking {
    launch { println("Espresso start"); delay(300); println("Espresso end") }
    launch { println("Tea start"); delay(100); println("Tea end") }
    launch { println("Latte start"); delay(200); println("Latte end") }
}

fun q2Sleep() = runBlocking {
    launch { println("Espresso start"); Thread.sleep(300); println("Espresso end") }
    launch { println("Tea start"); Thread.sleep(100); println("Tea end") }
    launch { println("Latte start"); Thread.sleep(200); println("Latte end") }
}

// Question 3: Chain of suspend functions
suspend fun loadUserSimple() { println("Loading user"); delay(100); checkPasswordSimple() }
suspend fun checkPasswordSimple() { println("Checking password"); delay(100); openProfileSimple() }
suspend fun openProfileSimple() { println("Opening profile") }
suspend fun q3() = loadUserSimple()

// Question 4: Calling suspend from normal code
suspend fun fetchUser() = "User"
fun q4Fix1() = runBlocking { fetchUser() }
fun q4Fix2() { CoroutineScope(Dispatchers.Default).launch { fetchUser() } }

// Question 5: Suspend is not a thread
suspend fun getThread() = Thread.currentThread().name
fun q5() = runBlocking {
    println("Before: ${Thread.currentThread().name}")
    println("Inside suspend: ${getThread()}")
}

// Question 6: Ten downloads, one thread
fun q6() = runBlocking {
    for (i in 1..10) {
        launch {
            println("download $i started")
            delay(500)
            println("download $i finished")
        }
    }
}

// Question 7: Order of launch
fun q7() = runBlocking {
    println("1")
    launch {
        println("2")
        delay(100)
        println("3")
    }
    println("4")
    delay(50)
    println("5")
}

// Question 8: Parent waits for child
fun q8() = runBlocking {
    val parent = launch {
        launch {
            delay(200)
            println("child done")
        }
        println("parent body done")
    }
    parent.join()
    println("report completed")
}

// Question 9: Screen closed, requests cancelled
fun q9() = runBlocking {
    val parent = launch {
        launch { try { delay(1000) } finally { println("Request 1 cleanup") } }
        launch { try { delay(1000) } finally { println("Request 2 cleanup") } }
    }
    delay(100)
    parent.cancelAndJoin()
    println("end")
}

// Question 10: Profile ready
fun q10() = runBlocking {
    coroutineScope {
        launch { delay(200); println("Avatar loaded") }
        launch { delay(100); println("Settings loaded") }
    }
    println("Profile ready")
    launch { delay(100); println("Extra coroutine finished") }
    println("end of main")
}

// Question 11: Reusable counter
fun CoroutineScope.counter(name: String, count: Int) = launch {
    for (i in 1..count) {
        delay(100)
        println("$name: $i")
    }
}
fun q11() = runBlocking {
    coroutineScope {
        counter("Counter 1", 3)
        counter("Counter 2", 2)
        println("launched")
    }
}

// Question 12: Returns at once or waits
fun CoroutineScope.bgGreeter(name: String) = launch { delay(100); println("Greeter: $name") }
suspend fun greetAll(names: List<String>) = coroutineScope {
    for (name in names) launch { delay(100); println("Greeter: $name") }
}
fun q12() = runBlocking {
    bgGreeter("Alice")
    println("Helper 1 returned immediately")
    delay(200)
    greetAll(listOf("Bob", "Charlie"))
    println("Helper 2 waited until finished")
}

// Question 13: Five builders
fun q13() {
    runBlocking {
        val j = launch { delay(50) }
        val d = async { delay(50); "async result" }
        val s = coroutineScope { delay(50); "scope result" }
        println("launch: $j")
        println("async: $d")
        println("coroutineScope: $s")
        d.await()
    }
}

// Question 14: Job or Deferred
fun q14() = runBlocking {
    val j = launch { delay(100); 6 * 7 }
    println("launch returns Job: $j (cannot get result)")
    val d = async { delay(100); 6 * 7 }
    println("async returns Deferred: ${d.await()}")
}

// Question 15: Three exports in a scope
fun q15() = runBlocking {
    println("Start")
    coroutineScope {
        launch { delay(300); println("X") }
        launch { delay(100); println("Y") }
        launch { delay(200); println("Z") }
    }
    println("All done")
}

fun main() = runBlocking {
    println("--- Question 1 ---"); q1()
    println("\n--- Question 2 (Delay) ---"); q2Delay()
    println("\n--- Question 2 (Sleep) ---"); q2Sleep()
    println("\n--- Question 3 ---"); q3()
    println("\n--- Question 5 ---"); q5()
    println("\n--- Question 6 ---"); q6()
    println("\n--- Question 7 ---"); q7()
    println("\n--- Question 8 ---"); q8()
    println("\n--- Question 9 ---"); q9()
    println("\n--- Question 10 ---"); q10()
    println("\n--- Question 11 ---"); q11()
    println("\n--- Question 12 ---"); q12()
    println("\n--- Question 13 ---"); q13()
    println("\n--- Question 14 ---"); q14()
    println("\n--- Question 15 ---"); q15()
}
