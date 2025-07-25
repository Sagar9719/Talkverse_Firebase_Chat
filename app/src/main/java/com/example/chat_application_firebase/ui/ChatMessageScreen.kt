package com.example.chat_application_firebase.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chat_application_firebase.R
import com.example.chat_application_firebase.extensions.ClickableExtensions.safeClickable
import com.example.chat_application_firebase.extensions.TimeExtension.toFormattedTime
import com.example.chat_application_firebase.viewmodel.ChatListViewModel
import com.example.chat_application_firebase.viewmodel.ChatMessageViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlin.invoke

internal typealias MessageSendClickCallback = (String) -> Unit

@Composable
fun ChatMessageScreen(
    chatMessageViewModel: ChatMessageViewModel = hiltViewModel(),
    senderId: String,
    receiverId: String,
    userName: String,
    onBackPress: () -> Unit
) {
    val state = chatMessageViewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val initialMessage by remember { mutableStateOf(value = "") }
    val isKeyboardVisible by rememberKeyboardVisibilityState()

    LaunchedEffect(key1 = Unit) {
        chatMessageViewModel.listenForMessages(senderId, receiverId)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .imePadding(),
        containerColor = Color(color = 0xFF121212),
        contentWindowInsets = WindowInsets(left = 0.dp),
        topBar = {
            ChatMessageTopBar(userName = userName, onBackPress = onBackPress)
        },
        bottomBar = {
            ChatMessageBottomBar(
                initialMessage = initialMessage,
                onSendClick = { message ->
                    if (message.length > 50) {
                        Toast.makeText(context, "Character Count Limit Reached", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        if (message.isNotEmpty()) {
                            chatMessageViewModel.updateMessageToDB(
                                message = message,
                                senderId = senderId,
                                receiverId = receiverId
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        ChatMessageBody(
            modifier = Modifier.padding(paddingValues = innerPadding),
            chatMessageViewModel = chatMessageViewModel,
            senderId = senderId,
            isKeyboardOpen = isKeyboardVisible
        )
    }
}

@Composable
fun ChatMessageBody(
    modifier: Modifier,
    chatMessageViewModel: ChatMessageViewModel,
    senderId: String,
    isKeyboardOpen: Boolean
) {
    val messages by remember { derivedStateOf { chatMessageViewModel.messages } }
    val listState = rememberLazyListState()

    LaunchedEffect(key1 = messages.size) {
        listState.animateScrollToItem(index = messages.lastIndex.coerceAtLeast(minimumValue = 0))
    }

    LaunchedEffect(key1 = isKeyboardOpen, key2 = messages.size) {
        if (isKeyboardOpen) {
            delay(timeMillis = 100)
            listState.animateScrollToItem(
                index = messages.lastIndex.coerceAtLeast(minimumValue = 0)
            )
        }
    }

    LazyColumn(
        modifier = modifier.padding(horizontal = 15.dp),
        state = listState,
        reverseLayout = false
    ) {
        items(items = messages, key = { it.timestamp?.seconds ?: it.hashCode().toLong() }) { msg ->
            Box(modifier = Modifier.animateItem()) {
                if (msg.senderId == senderId) {
                    SentMessageBubble(message = msg.message, timestamp = msg.timestamp)
                } else {
                    ReceivedMessageBubble(message = msg.message, timestamp = msg.timestamp)
                }
            }
        }
    }
}

@Composable
fun SentMessageBubble(message: String, timestamp: Timestamp?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = message,
            modifier = Modifier
                .background(Color(color = 0xFFDCF8C6), shape = RoundedCornerShape(size = 12.dp))
                .padding(all = 12.dp),
            color = Color.Black
        )
        timestamp?.toFormattedTime()?.let {
            Text(
                text = it,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ReceivedMessageBubble(message: String, timestamp: Timestamp?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = message,
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(size = 12.dp))
                .border(width = 1.dp, Color.LightGray, shape = RoundedCornerShape(size = 12.dp))
                .padding(all = 12.dp),
            color = Color.Black
        )
        timestamp?.toFormattedTime()?.let {
            Text(
                text = it,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}


@Composable
fun ChatMessageTopBar(userName: String, onBackPress: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(color = 0xFF1C1C1C))
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            modifier = Modifier
                .safeClickable(onClick = onBackPress)
                .size(size = 50.dp),
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = "Backpress Icon",
            tint = Color.White
        )

        Image(
            modifier = Modifier
                .padding(start = 1.dp)
                .size(size = 45.dp)
                .border(width = 1.dp, color = Color(color = 0xFF00C896), shape = CircleShape)
                .padding(bottom = 2.dp),
            painter = painterResource(id = R.drawable.user_avatar),
            contentDescription = "User Icon"
        )

        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = userName,
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color(color = 0xFFEDEDED),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
        )
    }
}


@Composable
@Preview
fun ChatMessageTopBarPreview() {
    ChatMessageTopBar(userName = "Test User", onBackPress = {})
}

@Composable
fun ChatMessageScreenPreview() {
    ChatMessageScreen(
        senderId = "1234",
        receiverId = "12345",
        userName = "Test1234",
        onBackPress = {})
}

@Composable
fun rememberKeyboardVisibilityState(): State<Boolean> {
    val currentDensity = LocalDensity.current
    val ime = WindowInsets.ime
    val imeVisible by remember {
        derivedStateOf {
            ime.getBottom(density = currentDensity) > 0
        }
    }
    return rememberUpdatedState(newValue = imeVisible)
}