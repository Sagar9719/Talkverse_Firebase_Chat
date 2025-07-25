package com.example.model

import com.google.firebase.Timestamp

data class ChatMessage(
    val message: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val timestamp: Timestamp? = null
)

