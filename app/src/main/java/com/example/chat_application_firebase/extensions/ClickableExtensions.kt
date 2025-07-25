package com.example.chat_application_firebase.extensions

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

@Stable
fun Modifier.safeClickable(
    debounceDuration: Long = 800L,
    enable: Boolean = true,
    onClick: () -> Unit
) = composed {
    var lastClickTime by remember { mutableLongStateOf(value = 0L) }
    this.clickable(enabled = enable) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= debounceDuration) {
            lastClickTime = currentTime
            onClick()
        }
    }
}