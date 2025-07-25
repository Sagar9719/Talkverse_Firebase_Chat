package com.example.chat_application_firebase.activity

import AppNavHost
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.chat_application_firebase.ui.theme.Chat_Application_FirebaseTheme
import com.example.chat_application_firebase.viewmodel.ChatListViewModel
import com.example.chat_application_firebase.viewmodel.ChatMessageViewModel
import com.example.chat_application_firebase.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private val loginViewModel: LoginViewModel by viewModels()
    private val chatListViewModel: ChatListViewModel by viewModels()

    private val chatMessageViewModel: ChatMessageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Chat_Application_FirebaseTheme {
                val isLoggedIn = firebaseAuth.currentUser != null
                Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->
                    AppNavHost(
                        loginViewModel = loginViewModel,
                        isUserLoggedIn = isLoggedIn,
                        modifier = Modifier.Companion.padding(paddingValues = innerPadding),
                        chatListViewModel = chatListViewModel,
                        chatMessageViewModel = chatMessageViewModel
                    )
                }
            }
        }
    }
}