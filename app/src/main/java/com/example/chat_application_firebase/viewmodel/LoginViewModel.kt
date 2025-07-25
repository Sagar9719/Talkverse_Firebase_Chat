package com.example.chat_application_firebase.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<Unit>>()
    val loginResult: LiveData<Result<Unit>> = _loginResult

    private val _registrationResult = MutableLiveData<Result<Unit>>()
    val registrationResult: LiveData<Result<Unit>> = _registrationResult

    fun loginUser(email: String, password: String) {
        _loginResult.value = Result.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginResult.value = Result.Success(Unit)
                } else {
                    val message = task.exception?.localizedMessage ?: "Login failed"
                    _loginResult.value = Result.Error(message)
                }
            }
    }

    fun registerUser(email: String, password: String) {
        _registrationResult.value = Result.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _registrationResult.value = Result.Success(Unit)
                } else {
                    val message = task.exception?.localizedMessage ?: "Registration failed"
                    _registrationResult.value = Result.Error(message)
                }
            }
    }

    sealed class Result<out T> {
        object Loading : Result<Nothing>()
        data class Success<out T>(val data: T) : Result<T>()
        data class Error(val message: String) : Result<Nothing>()
    }
}