package com.example.chat_application_firebase.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chat_application_firebase.R
import com.example.chat_application_firebase.common.ChatInputField
import com.example.chat_application_firebase.extensions.isValidPassword
import com.example.chat_application_firebase.extensions.safeClickable
import com.example.chat_application_firebase.utils.ChatUtils
import com.example.chat_application_firebase.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    navigateToBasicDetailsScreen: () -> Unit
) {
    val context = LocalContext.current
    val state = loginViewModel.state.collectAsStateWithLifecycle()
    val email = remember {
        mutableStateOf(
            value = TextFieldValue(text = "", selection = TextRange(index = 0))
        )
    }

    val password = remember {
        mutableStateOf(
            value = TextFieldValue(text = "", selection = TextRange(index = 0))
        )
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.4f)
                .clip(shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    clip = true
                )
                .background(Color.Black)
        ) {
            Image(
                painter = painterResource(id = R.drawable.blue_bg),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(space = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hey There! Let’s Log In to TalkVerse",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                textAlign = TextAlign.Center
            )

            ChatInputField(
                label = "Enter Your Email",
                placeholder = "Email",
                valueState = email,
                interactionSource = interactionSource,
                keyboardType = KeyboardType.Email,
                errorText = "Please enter correct email",
                isShowError = state.value.isEmailInvalid,
                isPasswordField = false,
                updateInvalidState = {
                    loginViewModel.updateEmailInvalidState(value = false)
                }
            )

            ChatInputField(
                label = "Enter Your Password",
                placeholder = "Password",
                valueState = password,
                interactionSource = interactionSource,
                keyboardType = KeyboardType.Password,
                errorText = "Please enter correct password",
                isShowError = state.value.isPasswordInvalid,
                isPasswordField = true,
                updateInvalidState = {
                    loginViewModel.updatePasswordInvalidState(value = false)
                }
            )

            Spacer(modifier = Modifier.height(height = 16.dp))

            Button(
                onClick = {
                    val passwordText = password.value.text
                    val emailText = email.value.text

                    when {
                        !passwordText.isValidPassword() -> {
                            ChatUtils.showToast(
                                context = context,
                                message = "Password must contain at least one number, one letter, and one special character (@#\$%&*!...)"
                            )
                            loginViewModel.updatePasswordInvalidState(value = true)
                        }

                        !android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches() -> {
                            ChatUtils.showToast(
                                context = context,
                                message = "Please enter a valid email address"
                            )
                            loginViewModel.updateEmailInvalidState(value = true)
                        }

                        else -> {
                            loginViewModel.triggerLogin(
                                email = email.value.text,
                                password = password.value.text
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(size = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Text(
                modifier = Modifier.safeClickable(onClick = onSignUpClick),
                text = "Create Your TalkVerse Account",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }

    ObserveSideEffects(
        loginViewModel = loginViewModel,
        onLoginSuccess = onLoginSuccess,
        navigateToBasicDetailsScreen = navigateToBasicDetailsScreen
    )
}

@Composable
private fun ObserveSideEffects(
    loginViewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    navigateToBasicDetailsScreen: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        loginViewModel.sideEffect.collectLatest {
            when (it) {
                is LoginViewModel.SideEffects.LoginSuccess -> {
                    ChatUtils.showToast(context = context, message = "Login Successfully")
                    onLoginSuccess.invoke()
                }

                is LoginViewModel.SideEffects.NavigateToBasicDetailsScreen -> {
                    ChatUtils.showToast(context = context, message = "Login Successfully")
                    navigateToBasicDetailsScreen.invoke()
                }

                is LoginViewModel.SideEffects.Error -> {
                    ChatUtils.showToast(context = context, message = it.errorMessage)
                }

                else -> Unit
            }
        }
    }
}

@Composable
@Preview
fun LoginScreenPreview() {
    LoginScreen(onLoginSuccess = {}, onSignUpClick = {}, navigateToBasicDetailsScreen = {})
}