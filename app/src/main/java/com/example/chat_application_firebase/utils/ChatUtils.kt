package com.example.chat_application_firebase.utils

object ChatUtils {
    private var isUserAuthenticated: Boolean = false

    fun setIsUserAuthenticated(value: Boolean) {
        isUserAuthenticated = value
    }

    fun isUserAuthenticated() = isUserAuthenticated
}