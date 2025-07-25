package com.example.chat_application_firebase.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.chat_application_firebase.extensions.safeClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputField(
    label: String,
    placeholder: String,
    valueState: MutableState<TextFieldValue>,
    interactionSource: MutableInteractionSource,
    keyboardType: KeyboardType,
    errorText: String,
    isShowError: Boolean = false,
    isPasswordField: Boolean = false,
    updateInvalidState: () -> Unit
) {
    val showPassword = remember { mutableStateOf(value = false) }

    val currentVisualTransformation =
        if (isPasswordField && !showPassword.value) PasswordVisualTransformation()
        else VisualTransformation.None

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
            cursorBrush = SolidColor(value = Color.Black),
            visualTransformation = currentVisualTransformation,
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
                trailingIcon = {
                    if (isPasswordField) {
                        val icon =
                            if (showPassword.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description =
                            if (showPassword.value) "Hide password" else "Show password"
                        Icon(
                            imageVector = icon,
                            contentDescription = description,
                            modifier = Modifier
                                .safeClickable { showPassword.value = !showPassword.value }
                                .padding(start = 6.dp),
                            tint = Color.Black
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
                visualTransformation = currentVisualTransformation
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