package com.example.chat_application_firebase.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chat_application_firebase.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BasicDetailsScreen(
    loginViewModel: LoginViewModel = hiltViewModel(), onSuccess: () -> Unit
) {
    val state = loginViewModel.state.collectAsStateWithLifecycle()
    val interactionSource = remember { MutableInteractionSource() }
    val name = remember {
        mutableStateOf(
            value = TextFieldValue(text = "", selection = TextRange(index = 0))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(space = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter Basic Details",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )

        InputField(
            label = "Enter Your Name",
            placeholder = "Name",
            valueState = name,
            interactionSource = interactionSource,
            keyboardType = KeyboardType.Text,
            errorText = "Please enter name",
            isShowError = state.value.isNameValid,
            updateInvalidState = {
                loginViewModel.updateNameInvalidState(value = false)
            }
        )

        Spacer(modifier = Modifier.height(height = 16.dp))

        Button(
            onClick = {
                loginViewModel.saveUserDataToFireStore(
                    name = name.value.text
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(size = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Text(
                text = "Next",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }

    ObserveSideEffects(
        loginViewModel = loginViewModel,
        onSuccess = onSuccess
    )
}

@Composable
private fun ObserveSideEffects(
    loginViewModel: LoginViewModel,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        loginViewModel.sideEffect.collectLatest {
            when (it) {
                is LoginViewModel.SideEffects.NameUpdatedSuccess -> {
                    Toast.makeText(context, "Name Updated Successfully", Toast.LENGTH_SHORT).show()
                    onSuccess.invoke()
                }

                is LoginViewModel.SideEffects.Error -> {
                    Toast.makeText(context, it.errorMessage, Toast.LENGTH_SHORT).show()
                }

                else -> Unit
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputField(
    label: String,
    placeholder: String,
    valueState: MutableState<TextFieldValue>,
    interactionSource: MutableInteractionSource,
    keyboardType: KeyboardType,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    errorText: String,
    isShowError: Boolean = false,
    updateInvalidState: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            ),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, bottom = 6.dp)
        )

        BasicTextField(
            value = valueState.value,
            onValueChange = {
                updateInvalidState.invoke()
                valueState.value = it
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
            singleLine = true,
            cursorBrush = SolidColor(Color.Black),
            visualTransformation = visualTransformation,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
        ) { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = valueState.value.text,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                shape = RoundedCornerShape(size = 24.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                placeholder = {
                    if (valueState.value.text.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(color = 0xFFE0E0E0),
                    unfocusedContainerColor = Color(color = 0xFFF0F0F0),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black
                ),
                interactionSource = interactionSource,
                visualTransformation = visualTransformation
            )
        }

        if (isShowError) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Red
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, bottom = 6.dp, top = 7.dp)
            )
        }
    }
}