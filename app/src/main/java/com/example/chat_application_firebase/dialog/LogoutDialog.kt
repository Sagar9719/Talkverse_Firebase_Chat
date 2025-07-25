package com.example.chat_application_firebase.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LogoutDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirmLogout: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = Color(color = 0xFF2C2C2C),
            shape = RoundedCornerShape(size = 16.dp),
            title = {
                Text(
                    text = "Logout?",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to logout?",
                    color = Color(color = 0xFFE0E0E0)
                )
            },
            confirmButton = {
                Button(
                    modifier = Modifier.background(
                        color = Color.Red,
                        shape = RoundedCornerShape(size = 8.dp)
                    ),
                    onClick = onConfirmLogout,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(size = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(text = "Logout", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    modifier = Modifier.background(
                        color = Color(color = 0xFFB0B0B0),
                        shape = RoundedCornerShape(size = 8.dp)
                    ),
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(size = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(text = "Cancel", color = Color.Black)
                }
            }
        )
    }
}


@Composable
@Preview
fun LogoutDialogPreview() {
    LogoutDialog(showDialog = true, onDismiss = {}, onConfirmLogout = {})
}