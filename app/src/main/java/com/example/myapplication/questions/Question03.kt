package com.example.myapplication.questions

import kotlinx.coroutines.delay

suspend fun loadUser() {
    println("Loading user...")
    delay(100)
    checkPassword()
}

suspend fun checkPassword() {
    println("Checking password...")
    delay(100)
    openProfile()
}

suspend fun openProfile() {
    println("Opening profile...")
    delay(100)
    println("Profile opened successfully!")
}

suspend fun main() {
    loadUser()
}
