package com.example.chat_application_firebase.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.chat_application_firebase.extensions.safeClickable

@Composable
fun LogoutDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirmLogout: () -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        color = Color(color = 0xFF2C2C2C),
                        shape = RoundedCornerShape(size = 16.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .padding(all = 20.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(space = 16.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Are you sure you want to logout??",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "You can re-login using the same number or by using another number",
                        color = Color(color = 0xFFE0E0E0),
                        textAlign = TextAlign.Center,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .safeClickable(onClick = onDismiss)
                                .background(
                                    color = Color(color = 0xFFB0B0B0),
                                    shape = RoundedCornerShape(size = 24.dp)
                                )
                                .padding(horizontal = 35.dp, vertical = 8.dp),
                        ) {
                            Text(text = "Cancel", color = Color.Black, style = MaterialTheme.typography.titleSmall)
                        }

                        Spacer(modifier = Modifier.weight(weight = 0.1f))

                        Row(
                            modifier = Modifier
                                .safeClickable(onClick = onConfirmLogout)
                                .background(
                                    color = Color(color = 0xFFB02D2D),
                                    shape = RoundedCornerShape(size = 24.dp)
                                )
                                .padding(horizontal = 35.dp, vertical = 8.dp),
                        ) {
                            Text(text = "Logout", color = Color.White, style = MaterialTheme.typography.titleSmall)
                        }
                    }
                }
            }
        }
    }
}


@Composable
@Preview
fun LogoutDialogPreview() {
    LogoutDialog(showDialog = true, onDismiss = {}, onConfirmLogout = {})
}