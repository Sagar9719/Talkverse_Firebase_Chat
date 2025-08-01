import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chat_application_firebase.dialog.LogoutDialog
import com.example.chat_application_firebase.screens.Screen
import com.example.chat_application_firebase.ui.BasicDetailsScreen
import com.example.chat_application_firebase.ui.ChatListScreen
import com.example.chat_application_firebase.ui.ChatMessageScreen
import com.example.chat_application_firebase.ui.LoginScreen
import com.example.chat_application_firebase.ui.SignUpScreen
import com.example.chat_application_firebase.utils.ChatUtils
import com.example.chat_application_firebase.viewmodel.ChatListViewModel
import com.example.chat_application_firebase.viewmodel.ChatMessageViewModel
import com.example.chat_application_firebase.viewmodel.LoginViewModel

@Composable
fun AppNavHost(
    loginViewModel: LoginViewModel,
    chatListViewModel: ChatListViewModel,
    isUserLoggedIn: Boolean,
    modifier: Modifier = Modifier
) {
    val startDestination = if (isUserLoggedIn) Screen.ChatList.route else Screen.Login.route
    val navController = rememberNavController()
    val showLogoutDialog = remember { mutableStateOf(value = false) }
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(
                loginViewModel = loginViewModel, onLoginSuccess = {
                    navController.navigate(route = Screen.ChatList.route) {
                        popUpTo(route = Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSignUpClick = {
                    navController.navigate(route = Screen.SignUp.route) {
                        popUpTo(route = Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToBasicDetailsScreen = {
                    navController.navigate(route = Screen.BasicDetailsScreen.route) {
                        popUpTo(route = Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = Screen.SignUp.route) {
            SignUpScreen(
                loginViewModel = loginViewModel, onSignUpSuccess = {
                    navController.navigate(route = Screen.Login.route) {
                        popUpTo(route = Screen.SignUp.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onLoginClick = {
                    navController.navigate(route = Screen.Login.route) {
                        popUpTo(route = Screen.SignUp.route) { inclusive = true }
                        launchSingleTop = true
                    }
                })
        }

        composable(route = Screen.BasicDetailsScreen.route) {
            BasicDetailsScreen(loginViewModel = loginViewModel, onSuccess = {
                navController.navigate(route = Screen.ChatList.route) {
                    popUpTo(route = Screen.BasicDetailsScreen.route) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }

        composable(route = Screen.ChatList.route) {
            ChatListScreen(
                chatListViewModel = chatListViewModel,
                onClick = { senderId, receiverId, userName ->
                    navController.navigate(route = "${Screen.ChatMessage.route}/$senderId/$receiverId/$userName")
                },
                onLogoutClick = {
                    showLogoutDialog.value = true
                }
            )
        }

        composable(
            route = Screen.ChatMessage.route + "/{senderId}/{receiverId}/{userName}",
            arguments = listOf(
                navArgument(name = "senderId") { type = NavType.StringType },
                navArgument(name = "receiverId") { type = NavType.StringType },
                navArgument(name = "userName") { type = NavType.StringType }
            )
        ) {
            val senderId = it.arguments?.getString("senderId") ?: ""
            val receiverId = it.arguments?.getString("receiverId") ?: ""
            val userName = it.arguments?.getString("userName") ?: ""
            ChatMessageScreen(
                senderId = senderId,
                receiverId = receiverId,
                userName = userName,
                onBackPress = {
                    navController.popBackStack()
                }
            )
        }
    }

    if (showLogoutDialog.value) {
        LogoutDialog(
            showDialog = showLogoutDialog.value,
            onDismiss = {
                showLogoutDialog.value = false
            },
            onConfirmLogout = {
                showLogoutDialog.value = false
                chatListViewModel.performLogOut(onSuccessLogout = {
                    ChatUtils.showToast(context = context, message = "Logged out successfully")
                    navController.navigate(route = Screen.Login.route) {
                        popUpTo(id = 0) { inclusive = true }
                        launchSingleTop = true
                    }
                })
            }
        )
    }
}
