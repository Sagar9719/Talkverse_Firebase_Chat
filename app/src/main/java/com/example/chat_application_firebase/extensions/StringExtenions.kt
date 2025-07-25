package com.example.chat_application_firebase.extensions

fun String.isValidPassword(): Boolean {
    val passwordRegex = Regex(pattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#\$%^&+=!]).{6,}\$")
    return passwordRegex.matches(input = this)
}