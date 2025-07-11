package com.example.plauenblod.feature.chat.model

data class Message(
    val id: String? = null,
    val chatId: String? = null,
    val senderId: String? = null,
    val recipientId: String? = null,
    val messageText: String? = null,
    val reactions: Map<String, String> = emptyMap(),
    val timeStamp: Long = System.currentTimeMillis(),
    val routeId: String? = null
)