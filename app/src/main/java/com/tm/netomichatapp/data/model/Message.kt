package com.tm.netomichatapp.data.model

data class Message(
    val text: String,
    val status: MessageStatus,
)

enum class MessageStatus {
    QUEUED, SENT, RECEIVED
}
