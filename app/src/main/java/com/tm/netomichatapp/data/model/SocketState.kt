package com.tm.netomichatapp.data.model

sealed class SocketState {
    object Disconnected : SocketState()
    object Connecting : SocketState()
    object Connected : SocketState()
}
