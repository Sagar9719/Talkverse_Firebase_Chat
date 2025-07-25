package com.example.chat_application_firebase.viewmodel

import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) : BaseViewModel<LoginViewModel.State, LoginViewModel.SideEffects>() {
    data class State(
        val isLoading: Boolean = false,
        val isEmailInvalid: Boolean = false,
        val isPasswordInvalid: Boolean = false,
        val isNameValid: Boolean = false
    )

    sealed class SideEffects {
        data class Error(val errorMessage: String) : SideEffects()
        data object LoginSuccess : SideEffects()
        data object SignUpSuccess : SideEffects()
        data object NavigateToBasicDetailsScreen : SideEffects()

        data object NameUpdatedSuccess : SideEffects()
    }

    override fun setDefaultState(): State = State()

    fun updateLoadingState(value: Boolean) {
        updateState {
            it.copy(isLoading = value)
        }
    }

    fun loginUser(email: String, password: String) {
        updateLoadingState(value = true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateLoadingState(value = false)
                    checkIfUserProfileExists(
                        onExists = {
                            postSideEffect(sideEffect = SideEffects.LoginSuccess)
                        },
                        onDoesNotExist = {
                            postSideEffect(sideEffect = SideEffects.NavigateToBasicDetailsScreen)
                        }
                    )
                } else {
                    val message = task.exception?.localizedMessage ?: "Login failed"
                    updateLoadingState(value = false)
                    postSideEffect(sideEffect = SideEffects.Error(errorMessage = message))
                }
            }
    }

    fun registerUser(email: String, password: String) {
        updateLoadingState(value = true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateLoadingState(value = false)
                    postSideEffect(sideEffect = SideEffects.SignUpSuccess)
                } else {
                    val message = task.exception?.localizedMessage ?: "Registration failed"
                    updateLoadingState(value = false)
                    postSideEffect(sideEffect = SideEffects.Error(errorMessage = message))
                }
            }
    }

    fun updateEmailInvalidState(value: Boolean) {
        updateState { it.copy(isEmailInvalid = value) }
    }

    fun updatePasswordInvalidState(value: Boolean) {
        updateState { it.copy(isPasswordInvalid = value) }
    }

    fun updateNameInvalidState(value: Boolean) {
        updateState { it.copy(isNameValid = value) }
    }

    fun triggerLogin(email: String, password: String) {
        validateCredentials(email, password) {
            loginUser(email = email, password = password)
        }
    }

    fun triggerSignUp(email: String, password: String) {
        validateCredentials(email, password) {
            registerUser(email = email, password = password)
        }
    }

    private fun validateCredentials(
        email: String,
        password: String,
        onValid: () -> Unit
    ) {
        val isEmailEmpty = email.isEmpty()
        val isPasswordEmpty = password.isEmpty()

        updateEmailInvalidState(value = isEmailEmpty)
        updatePasswordInvalidState(value = isPasswordEmpty)

        if (!isEmailEmpty && !isPasswordEmpty) {
            onValid()
        }
    }

    fun checkIfUserProfileExists(
        onExists: () -> Unit,
        onDoesNotExist: () -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return

        fireStore.collection("users").document(uid).get()
            .addOnSuccessListener { data ->
                if (data.exists() && data.getString("name")?.isNotEmpty() == true) {
                    onExists.invoke()
                } else {
                    onDoesNotExist.invoke()
                }
            }
            .addOnFailureListener {
                onDoesNotExist.invoke()
            }
    }

    fun saveUserDataToFireStore(name: String) {
        val user = auth.currentUser
        val uid = user?.uid ?: return
        if (name.isEmpty()) {
            updateNameInvalidState(value = true)
            return
        }

        val userDetails = hashMapOf(
            "uid" to uid,
            "name" to name,
            "email" to user.email
        )

        fireStore.collection("users").document(uid).set(userDetails)
            .addOnSuccessListener {
                postSideEffect(sideEffect = SideEffects.NameUpdatedSuccess)
            }
            .addOnFailureListener {
                val message = it.localizedMessage ?: "User name updation failed"
                postSideEffect(sideEffect = SideEffects.Error(errorMessage = message))
            }
    }
}