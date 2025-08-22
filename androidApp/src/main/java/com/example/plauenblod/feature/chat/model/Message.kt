package com.example.plauenblod.feature.chat.model

import com.example.plauenblod.android.util.FirestoreInstant

data class Message(
    val id: String? = null,
    val chatId: String? = null,
    val senderId: String? = null,
    val recipientId: String? = null,
    val messageText: String? = null,
    val reactions: Map<String, String> = emptyMap(),
    val timeStamp: FirestoreInstant? = null,
    val routeId: String? = null,
    val collectionId: String? = null,
)