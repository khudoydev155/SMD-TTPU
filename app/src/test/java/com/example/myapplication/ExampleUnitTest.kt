package com.example.myapplication

import com.example.myapplication.questions.*
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testCoroutinesPractice() = runBlocking {
        println("=== Q1 ==="); q1()
        println("=== Q2 (Delay) ==="); q2Delay()
        println("=== Q3 ==="); q3()
        println("=== Q5 ==="); q5()
        println("=== Q6 ==="); q6()
        println("=== Q7 ==="); q7()
        println("=== Q8 ==="); q8()
        println("=== Q9 ==="); q9()
        println("=== Q10 ==="); q10()
        println("=== Q11 ==="); q11()
        println("=== Q12 ==="); q12()
        println("=== Q13 ==="); q13()
        println("=== Q14 ==="); q14()
        println("=== Q15 ==="); q15()
    }
}
