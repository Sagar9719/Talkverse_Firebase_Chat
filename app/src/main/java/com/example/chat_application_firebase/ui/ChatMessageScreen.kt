package com.example.chat_application_firebase.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chat_application_firebase.R
import com.example.chat_application_firebase.extensions.safeClickable
import com.example.chat_application_firebase.extensions.toFormattedTime
import com.example.chat_application_firebase.message.MessageStatus
import com.example.chat_application_firebase.viewmodel.ChatMessageViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay

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

    LaunchedEffect(key1 = receiverId) {
        chatMessageViewModel.listenForMessages(senderId, receiverId)
    }

    LaunchedEffect(key1 = receiverId) {
        val unreadMessageIds = chatMessageViewModel.messages.filter {
            it.receiverId == senderId && it.status == MessageStatus.DELIVERED.name
        }.map { it.id }

        if (unreadMessageIds.isNotEmpty()) {
            chatMessageViewModel.markMessagesAsDelivered(
                senderId = senderId,
                receiverId = receiverId,
                messageIds = unreadMessageIds
            )
        }
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
                    if (message.length > 500) {
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
                    SentMessageBubble(
                        message = msg.message,
                        timestamp = msg.timestamp,
                        status = msg.status
                    )
                } else {
                    ReceivedMessageBubble(message = msg.message, timestamp = msg.timestamp)
                }
            }
        }
    }
}

@Composable
fun SentMessageBubble(message: String, timestamp: Timestamp?, status: String) {
    val statusIcon = when (status) {
        MessageStatus.PENDING.name -> Icons.Default.AccessTime
        MessageStatus.SENT.name -> Icons.Default.Check
        MessageStatus.DELIVERED.name -> Icons.Default.DoneAll
        MessageStatus.SEEN.name -> Icons.Default.DoneAll
        else -> null
    }

    val iconTint = when (status) {
        MessageStatus.SEEN.name -> Color(color = 0xFF00B2FF)
        else -> Color.Gray
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.End
    ) {
        Box(
            modifier = Modifier
                .widthIn(
                    min = Dp.Unspecified,
                    max = (LocalConfiguration.current.screenWidthDp.dp * 0.7f)
                )
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(color = 0xFFB2F7EF),
                            Color(color = 0xFF9DEFE0)
                        )
                    ),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 0.dp,
                        bottomEnd = 16.dp,
                        bottomStart = 16.dp
                    )
                )
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 0.dp,
                        bottomEnd = 16.dp,
                        bottomStart = 16.dp
                    )
                )
                .padding(all = 12.dp)
        ) {
            Text(
                text = message,
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 20.sp
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            timestamp?.toFormattedTime()?.let {
                Text(
                    text = it,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp, end = 4.dp)
                )
            }

            if (statusIcon != null) {
                Spacer(modifier = Modifier.width(width = 4.dp))

                Icon(
                    imageVector = statusIcon,
                    contentDescription = "Message status",
                    modifier = Modifier.size(size = 14.dp),
                    tint = iconTint
                )
            }
        }
    }
}


@Composable
fun ReceivedMessageBubble(message: String, timestamp: Timestamp?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(
                    min = Dp.Unspecified,
                    max = (LocalConfiguration.current.screenWidthDp.dp * 0.7f)
                )
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(color = 0xFFF1F1F1),
                            Color(color = 0xFFECECEC)
                        )
                    ),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 16.dp,
                        bottomEnd = 16.dp,
                        bottomStart = 16.dp
                    )
                )
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 16.dp,
                        bottomEnd = 16.dp,
                        bottomStart = 16.dp
                    ),
                    clip = false
                )
                .border(
                    width = 0.6.dp,
                    color = Color(color = 0xFFDDDDDD),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 16.dp,
                        bottomEnd = 16.dp,
                        bottomStart = 16.dp
                    )
                )
                .padding(all = 12.dp)
        ) {
            Text(
                text = message,
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 20.sp
                )
            )
        }

        timestamp?.toFormattedTime()?.let {
            Text(
                text = it,
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
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