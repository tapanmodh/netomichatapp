package com.tm.netomichatapp.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piesocket.channels.Channel
import com.piesocket.channels.PieSocket
import com.piesocket.channels.misc.PieSocketEvent
import com.piesocket.channels.misc.PieSocketEventListener
import com.piesocket.channels.misc.PieSocketOptions
import com.tm.netomichatapp.data.model.Message
import com.tm.netomichatapp.data.model.MessageStatus
import com.tm.netomichatapp.data.model.SocketState
import com.tm.netomichatapp.utils.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val _socketState: MutableStateFlow<SocketState> =
        MutableStateFlow(SocketState.Disconnected)
    val socketState: StateFlow<SocketState> = _socketState
    private var channel: Channel? = null

    val messageList by lazy {
        mutableStateListOf<Message>()
    }

    fun connectSocket() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_socketState.value == SocketState.Disconnected) {

                _socketState.update { SocketState.Connecting }

                val options = PieSocketOptions()
                options.clusterId = AppConstants.CLUSTER_ID
                options.apiKey = AppConstants.API_KEY

                channel = PieSocket(options).join(AppConstants.CHANNEL_NAME)
                listenForConnected()
                listenForNewMessage()
                listenForError()
            }
        }
    }

    fun listenForConnected() {
        channel?.listen(AppConstants.CONNECTED_EVENT, object : PieSocketEventListener() {
            override fun handleEvent(event: PieSocketEvent) {
                _socketState.update { SocketState.Connected }
                sendQueuedMessages()
            }
        })
    }

    fun listenForNewMessage() {

        channel?.listen(AppConstants.NEW_MESSAGE_EVENT, object : PieSocketEventListener() {
            override fun handleEvent(event: PieSocketEvent) {
                viewModelScope.launch(Dispatchers.IO) {
                    val messageIndex =
                        messageList.indexOfLast { it.text.contentEquals(event.data) && it.status == MessageStatus.QUEUED }
                    if (messageIndex != -1) {
                        messageList[messageIndex] =
                            messageList[messageIndex].copy(status = MessageStatus.SENT)
                    } else {
                        messageList.add(
                            Message(
                                text = event.data,
                                status = MessageStatus.RECEIVED
                            )
                        )
                    }
                }
            }
        })
    }

    fun listenForError() {
        channel?.listen(AppConstants.ERROR_EVENT, object : PieSocketEventListener() {
            override fun handleEvent(event: PieSocketEvent) {
                _socketState.update { SocketState.Disconnected }
            }
        })
    }

    fun sendMessage(message: String, fromQueue: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!fromQueue) {
                messageList.add(Message(text = message, status = MessageStatus.QUEUED))
            }
            if (_socketState.value == SocketState.Connected) {
                val newMessage = PieSocketEvent(AppConstants.NEW_MESSAGE_EVENT)
                newMessage.setData(message)

                channel?.publish(newMessage)
            }
        }
    }

    fun sendQueuedMessages() {
        val queuedList = messageList.filter { it.status == MessageStatus.QUEUED }
        queuedList.forEach {
            sendMessage(it.text, true)
        }
    }
}