import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chat_application_firebase.screens.Screen
import com.example.chat_application_firebase.ui.ChatListScreen
import com.example.chat_application_firebase.ui.LoginScreen
import com.example.chat_application_firebase.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavHost(
    loginViewModel: LoginViewModel,
    isUserLoggedIn: Boolean,
    modifier: Modifier = Modifier
) {
    val startDestination = if (isUserLoggedIn) Screen.ChatList.route else Screen.Login.route
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(loginViewModel = loginViewModel, onLoginSuccess = {
                navController.navigate(route = Screen.ChatList.route) {
                    popUpTo(route = Screen.Login.route) { inclusive = true }
                }
            })
        }

        composable(route = Screen.ChatList.route) {
            ChatListScreen()
        }
    }
}
