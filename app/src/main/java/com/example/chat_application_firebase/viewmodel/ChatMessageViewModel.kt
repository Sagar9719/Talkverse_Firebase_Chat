package com.example.chat_application_firebase.viewmodel

import androidx.compose.runtime.mutableStateListOf
import com.example.chat_application_firebase.message.MessageStatus
import com.example.chat_application_firebase.model.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatMessageViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) : BaseViewModel<ChatMessageViewModel.State, ChatMessageViewModel.SideEffects>() {
    private var listenerRegistration: ListenerRegistration? = null
    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    override fun setDefaultState(): State = State()
    data class State(
        val isLoading: Boolean = false,
        val senderId: String = "",
        val receiverId: String = ""
    )

    sealed class SideEffects {
        data object NetworkError : SideEffects()
    }

    fun listenForMessages(senderId: String, receiverId: String) {
        val chatId = getChatId(user1 = senderId, user2 = receiverId)

        listenerRegistration = fireStore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener

                _messages.clear()
                val unreadMessageIds = mutableListOf<String>()

                for (doc in snapshot.documents) {
                    val msg = doc.toObject(ChatMessage::class.java)?.copy(id = doc.id)
                    msg?.let {
                        _messages.add(it)
                        if (it.receiverId == senderId && it.status == MessageStatus.DELIVERED.name) {
                            unreadMessageIds.add(it.id)
                        }
                    }
                }

                if (unreadMessageIds.isNotEmpty()) {
                    markMessagesAsDelivered(
                        senderId = senderId,
                        receiverId = receiverId,
                        messageIds = unreadMessageIds
                    )
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    fun updateMessageToDB(message: String, senderId: String, receiverId: String) {
        val chatId = getChatId(user1 = senderId, user2 = receiverId)
        val messageData = hashMapOf(
            "message" to message,
            "senderId" to senderId,
            "receiverId" to receiverId,
            "timestamp" to FieldValue.serverTimestamp(),
            "status" to MessageStatus.SENT.name
        )

        val chatRef = fireStore.collection("chats")
            .document(chatId)
            .collection("messages")

        chatRef.add(messageData)
            .addOnSuccessListener { docRef ->
                docRef.update("status", MessageStatus.DELIVERED.name)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                val pendingMessageData = messageData.toMutableMap().apply {
                    this["status"] = MessageStatus.PENDING.name
                    this["timestamp"] = FieldValue.serverTimestamp()
                }

                chatRef.add(pendingMessageData)
                    .addOnSuccessListener {}
                    .addOnFailureListener { secondFail ->
                        secondFail.printStackTrace()
                    }
            }
    }

    fun getChatId(user1: String, user2: String): String {
        return if (user1 < user2) "$user1-$user2" else "$user2-$user1"
    }

    fun markMessagesAsDelivered(senderId: String, receiverId: String, messageIds: List<String>) {
        val chatId = getChatId(user1 = senderId, user2 = receiverId)
        val batch = fireStore.batch()

        messageIds.forEach { msgId ->
            val msgRef = fireStore.collection("chats")
                .document(chatId)
                .collection("messages")
                .document(msgId)
            batch.update(msgRef, "status", MessageStatus.SEEN.name)
        }

        batch.commit().addOnSuccessListener {
            _messages.replaceAll { message ->
                if (messageIds.contains(message.id)) {
                    message.copy(status = MessageStatus.SEEN.name)
                } else message
            }
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }
}