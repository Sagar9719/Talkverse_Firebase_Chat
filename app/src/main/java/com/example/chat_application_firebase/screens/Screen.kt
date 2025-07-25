package com.example.chat_application_firebase.screens

sealed class Screen(val route: String) {
    object Login: Screen(route = "login")
    object ChatList: Screen(route = "chat_list")
}