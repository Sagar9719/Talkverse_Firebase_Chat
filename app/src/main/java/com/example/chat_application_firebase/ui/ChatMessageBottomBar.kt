package com.example.chat_application_firebase.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.chat_application_firebase.R
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay

@Composable
fun ChatMessageBottomBar(
    initialMessage: String = "",
    onSendClick: MessageSendClickCallback = {},
    focusChangeListener: (FocusState) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val sendMessageModifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Transparent)
            .padding(vertical = 12.dp, horizontal = 16.dp)

        SendChatMessageContent(
            modifier = sendMessageModifier,
            initialMessage = initialMessage,
            focusChangeListener = focusChangeListener,
            onSendCLick = onSendClick,
        )
    }
}

@OptIn(markerClass = [ExperimentalMaterial3Api::class])
@Composable
fun SendChatMessageContent(
    modifier: Modifier,
    initialMessage: String,
    alwaysShowSendButton: Boolean = false,
    focusChangeListener: (FocusState) -> Unit = {},
    onSendCLick: MessageSendClickCallback = {},
    onTextChange: (String) -> Unit = {}
) {
    var messageValue by remember {
        mutableStateOf(
            value = TextFieldValue(
                text = initialMessage,
                selection = TextRange(index = initialMessage.length)
            )
        )
    }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = Unit) {
        focusRequester.freeFocus()
        awaitFrame()
        delay(timeMillis = 500)
        focusRequester.requestFocus()
        keyboardController?.hide()
    }

    LaunchedEffect(key1 = initialMessage) {
        if (initialMessage.isNotEmpty()) {
            messageValue = TextFieldValue(
                text = initialMessage,
                selection = TextRange(index = initialMessage.length)
            )
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(space = 16.dp)) {
        val scrollState = rememberScrollState()

        LaunchedEffect(key1 = messageValue) {
            onTextChange.invoke(messageValue.text)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
        ) {
            val iterationSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .weight(weight = 1f, fill = false)
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(size = 12.dp))
                    .background(
                        color = Color.Transparent, shape = RoundedCornerShape(size = 12.dp)
                    )
            ) {
                BasicTextField(
                    value = messageValue,
                    onValueChange = {
                        messageValue = it
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                    singleLine = false,
                    cursorBrush = SolidColor(value = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 100.dp)
                        .verticalScroll(scrollState, reverseScrolling = true)
                        .focusRequester(focusRequester)
                        .onFocusChanged(onFocusChanged = focusChangeListener)
                ) { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = messageValue.text,
                        innerTextField = innerTextField,
                        enabled = true,
                        singleLine = false,
                        visualTransformation = VisualTransformation.None,
                        interactionSource = iterationSource,
                        shape = RoundedCornerShape(size = 24.dp),
                        contentPadding = PaddingValues(
                            vertical = 8.dp,
                            horizontal = 12.dp
                        ),
                        placeholder = {
                            Text(
                                text = "Message",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.LightGray,
                            unfocusedContainerColor = Color.LightGray,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black
                        )
                    )
                }
            }

            val showSendButtonVisibility =
                remember(key1 = messageValue.text, key2 = alwaysShowSendButton) {
                    alwaysShowSendButton || messageValue.text.isNotBlank()
                }

            if (showSendButtonVisibility) {
                Spacer(modifier = Modifier.width(width = 14.dp))

                IconButton(
                    onClick = {
                        onSendCLick.invoke(messageValue.text)
                        messageValue = TextFieldValue(text = "")
                    },
                    modifier = Modifier
                        .align(alignment = Alignment.Bottom)
                        .background(
                            color = Color.Blue,
                            shape = CircleShape,
                        )
                        .size(size = 45.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(size = 40.dp)
                            .padding(all = 8.dp),
                        imageVector = Icons.Default.Send,
                        contentDescription = "Icon for community message screen send icon",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}