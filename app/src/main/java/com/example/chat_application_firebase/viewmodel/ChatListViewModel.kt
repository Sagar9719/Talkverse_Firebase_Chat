package com.example.chat_application_firebase.viewmodel

import com.example.chat_application_firebase.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) :
    BaseViewModel<ChatListViewModel.State, ChatListViewModel.SideEffects>() {
    data class State(
        val isLoading: Boolean = false,
        val chatList: List<UserModel> = emptyList(),
        val currentUserName: String = "",
        val currentUserId: String = "",
        val currentUserEmail: String = ""
    )

    sealed class SideEffects {
        data class NetworkError(val errorMessage: String) : SideEffects()
    }

    override fun setDefaultState() = State()

    fun updateLoadingState(value: Boolean) {
        updateState {
            it.copy(isLoading = value)
        }
    }
    fun fetchCurrentUser() {
        updateLoadingState(value = true)
        val currentUser = firebaseAuth.currentUser
        val uid = currentUser?.uid

        if (uid == null) return

        fireStore.collection("users").document(uid).get()
            .addOnSuccessListener { data ->
                if (data.exists()) {
                    updateState {
                        it.copy(
                            currentUserId = data.id,
                            currentUserName = data.getString("name") ?: "",
                            currentUserEmail = data.getString("email") ?: ""
                        )
                    }
                }
                fetchAllUsers()
            }
            .addOnFailureListener { e ->
                updateLoadingState(value = false)
                val message = e.localizedMessage ?: "Fetch failed"
                postSideEffect(sideEffect = SideEffects.NetworkError(errorMessage = message))
                e.printStackTrace()
            }
    }

    fun fetchAllUsers() {
        fireStore.collection("users").get()
            .addOnSuccessListener { result ->
                val userList = mutableListOf<UserModel>()
                for (document in result) {
                    val user = document.toObject(UserModel::class.java)
                    userList.add(user)
                }

                val filteredList = userList.filter { it.uid != state.value.currentUserId }

                updateLoadingState(value = false)
                updateState {
                    it.copy(chatList = filteredList)
                }
            }
            .addOnFailureListener { e ->
                updateLoadingState(value = false)
                val message = e.localizedMessage ?: "Fetch failed"
                postSideEffect(sideEffect = SideEffects.NetworkError(errorMessage = message))
                e.printStackTrace()
            }
    }

    fun performLogOut(onSuccessLogout: () -> Unit) {
        firebaseAuth.signOut()
        onSuccessLogout()
    }
}