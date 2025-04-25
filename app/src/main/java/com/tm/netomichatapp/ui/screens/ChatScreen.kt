package com.tm.netomichatapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tm.netomichatapp.R
import com.tm.netomichatapp.data.model.Message
import com.tm.netomichatapp.data.model.MessageStatus
import com.tm.netomichatapp.data.model.SocketState
import com.tm.netomichatapp.ui.theme.ColorReceivedMessage
import com.tm.netomichatapp.ui.theme.ColorSentMessage
import com.tm.netomichatapp.ui.theme.Purple40
import com.tm.netomichatapp.ui.viewmodel.ChatViewModel

@Composable
fun ChatScreen(modifier: Modifier = Modifier, chatViewModel: ChatViewModel) {

    Column(modifier = modifier) {
        AppHeader()
        MessageList(modifier = Modifier.weight(1f), messageList = chatViewModel.messageList)
        MessageInput(
            onMessageSend = {
                chatViewModel.sendMessage(it)
            }
        )
        val socketState = chatViewModel.socketState.collectAsState()
        if (socketState.value != SocketState.Connected) {
            ConnectionStatus(socketState.value)
        }
    }
}

@Composable
fun ConnectionStatus(socketState: SocketState) {
    Box(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .background(Color.Red),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.Companion.padding(4.dp),
            text = if (socketState == SocketState.Disconnected) stringResource(R.string.disconnected) else stringResource(
                R.string.connecting
            ),
            color = Color.Companion.White,
            fontSize = 12.sp
        )
    }
}

@Composable
fun MessageList(modifier: Modifier = Modifier, messageList: List<Message>) {

    if (messageList.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(60.dp),
                painter = painterResource(id = R.drawable.ic_empty),
                contentDescription = stringResource(R.string.empty_message),
                tint = Purple40
            )
            Text(text = stringResource(R.string.empty_message), fontSize = 22.sp)
        }
    } else {
        LazyColumn(
            modifier = modifier,
            reverseLayout = true
        ) {
            items(messageList.reversed()) {
                MessageRow(message = it)
            }
        }
    }
}

@Composable
fun MessageRow(message: Message) {
    val isSent = message.status == MessageStatus.QUEUED || message.status == MessageStatus.SENT

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .align(
                        if (isSent) Alignment.BottomEnd else Alignment.BottomStart
                    )
                    .padding(
                        start = if (isSent) 70.dp else 8.dp,
                        end = if (isSent) 8.dp else 70.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                    .clip(RoundedCornerShape(48f))
                    .background(if (isSent) ColorSentMessage else ColorReceivedMessage)
                    .padding(16.dp)
            ) {
                Column {

                    Text(
                        text = message.text,
                        fontWeight = FontWeight.W500,
                        color = Color.White,
                        modifier = Modifier.padding(
                            start = 0.dp,
                            top = 0.dp,
                            end = 10.dp,
                            bottom = 0.dp
                        )
                    )
                    if (isSent) {
                        Row(
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(
                                modifier = Modifier.size(15.dp),
                                imageVector = if (message.status == MessageStatus.QUEUED) Icons.Outlined.Warning else Icons.Outlined.Check,
                                contentDescription = stringResource(R.string.message_status_icon),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageInput(onMessageSend: (String) -> Unit) {
    var message by remember {
        mutableStateOf("")
    }
    Row(
        modifier = Modifier.Companion.padding(8.dp),
        verticalAlignment = Alignment.Companion.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier.Companion.weight(1f),
            value = message,
            onValueChange = {
                message = it
            },
            placeholder = { Text(text = stringResource(R.string.message), color = Color.Gray) }
        )
        IconButton(onClick = {
            if (message.trim().isNotEmpty()) {
                onMessageSend(message.trim())
                message = ""
            }
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = stringResource(R.string.send)
            )
        }
    }
}

@Composable
fun AppHeader() {
    Box(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .background(Purple40)
    ) {
        Text(
            modifier = Modifier.Companion.padding(16.dp),
            text = stringResource(R.string.app_name),
            color = Color.Companion.White,
            fontSize = 22.sp
        )
    }
}