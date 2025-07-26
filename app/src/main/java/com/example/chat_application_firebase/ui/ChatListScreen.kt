package com.example.chat_application_firebase.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chat_application_firebase.R
import com.example.chat_application_firebase.extensions.safeClickable
import com.example.chat_application_firebase.viewmodel.ChatListViewModel
import com.example.chat_application_firebase.model.UserModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    chatListViewModel: ChatListViewModel = hiltViewModel(),
    onClick: (senderId: String, receiverId: String, userName: String) -> Unit,
    onLogoutClick: () -> Unit
) {
    val state = chatListViewModel.state.collectAsStateWithLifecycle()
    val chats = remember(key1 = state.value.chatList) { state.value.chatList }

    LaunchedEffect(key1 = Unit) {
        chatListViewModel.fetchCurrentUser()
    }

    Scaffold(
        topBar = {
            ChatTopBar(currentUserName = state.value.currentUserName, onLogoutClick = onLogoutClick)
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(color = 0xFF121212)
    ) { innerPadding ->
        ChatList(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize(),
            chatList = chats,
            onClick = onClick,
            senderId = state.value.currentUserId,
            isLoading = state.value.isLoading
        )
    }
}

@Composable
fun ChatTopBar(currentUserName: String, onLogoutClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(color = 0xFF1C1C1C))
                .padding(vertical = 18.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "TalkVerse 🗨️",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(space = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color(color = 0xFF00C896),
                    modifier = Modifier.size(size = 20.dp)
                )

                Text(
                    text = currentUserName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(color = 0xFFEDEDED),
                        fontWeight = FontWeight.Medium
                    )
                )

                Icon(
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .size(size = 22.dp)
                        .safeClickable(onClick = onLogoutClick),
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "User",
                    tint = Color(color = 0xFFEF5350)
                )
            }
        }

        HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color.Gray)

        Text(
            modifier = Modifier.padding(top = 10.dp, start = 12.dp),
            text = "ChatSpace",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp
            )
        )
    }
}


@Composable
@Preview
fun ChatTopBarPreview() {
    ChatTopBar(currentUserName = "Test User", onLogoutClick = {})
}

@Composable
fun ChatList(
    isLoading: Boolean,
    modifier: Modifier,
    chatList: List<UserModel>,
    senderId: String,
    onClick: (senderId: String, receiverId: String, userName: String) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .padding(vertical = 20.dp, horizontal = 12.dp)
            .background(Color(color = 0xFF121212))
    ) {
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center).size(size = 70.dp),
                        color = Color.White,
                        strokeWidth = 3.dp,
                    )
                }
            }
        } else {
            itemsIndexed(items = chatList) { index, userData ->
                ChatListItem(
                    name = userData.name,
                    senderId = senderId,
                    receiverId = userData.uid,
                    onClick = onClick
                )

                if (index != chatList.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 14.dp)
                            .fillMaxWidth(),
                        thickness = 1.dp,
                        color = Color(color = 0xFF4D4C4C)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatListItem(
    name: String,
    senderId: String,
    receiverId: String,
    onClick: (senderId: String, receiverId: String, userName: String) -> Unit
) {
    Row(
        modifier = Modifier
            .safeClickable(onClick = { onClick.invoke(senderId, receiverId, name) })
            .fillMaxWidth()
            .background(Color(color = 0xFF2C2C2C), shape = RoundedCornerShape(size = 16.dp))
            .border(
                width = 1.dp,
                Color(color = 0xFF3A3838),
                shape = RoundedCornerShape(size = 16.dp)
            )
            .padding(all = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(size = 50.dp)
                .border(width = 1.dp, color = Color(color = 0xFF00C896), shape = CircleShape)
                .padding(bottom = 2.dp),
            painter = painterResource(id = R.drawable.user_avatar),
            contentDescription = "User Icon"
        )

        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = name,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color(color = 0xFFEDEDED),
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
@Preview
fun ChatListItemPreview() {
    ChatListItem(
        name = "Sagar Singh",
        senderId = "1234",
        receiverId = "12345",
        onClick = { id1, id2, name -> })
}