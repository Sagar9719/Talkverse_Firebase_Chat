package com.example.chat_application_firebase.utils

import android.content.Context
import android.widget.Toast

object ChatUtils {
    private var isUserAuthenticated: Boolean = false

    fun setIsUserAuthenticated(value: Boolean) {
        isUserAuthenticated = value
    }

    fun isUserAuthenticated() = isUserAuthenticated

    fun showToast(context: Context, message: String) {
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}