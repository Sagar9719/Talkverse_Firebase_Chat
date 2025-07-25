package com.example.model

import com.example.chat_application_firebase.message.MessageStatus
import com.google.firebase.Timestamp

data class ChatMessage(
    val id: String = "",
    val message: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val timestamp: Timestamp? = null,
    val status: String = MessageStatus.DELIVERED.name
)

