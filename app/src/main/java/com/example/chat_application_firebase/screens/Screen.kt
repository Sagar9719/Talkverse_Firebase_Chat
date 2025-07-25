package com.example.chat_application_firebase.screens

sealed class Screen(val route: String) {
    object Login: Screen(route = "login")
    object ChatList: Screen(route = "chat_list")
    object SignUp: Screen(route = "signup")
    object BasicDetailsScreen: Screen(route = "basic_details_Screen")
    data object ChatMessage: Screen(route = "chat_message")
}