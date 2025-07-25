package com.example.chat_application_firebase.extensions

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

object TimeExtension {
    fun Timestamp?.toFormattedTime(): String {
        this ?: return ""
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(this.toDate())
    }
}