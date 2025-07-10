package com.example.plauenblod.feature.chat.model

data class Chat (
    val id: String? = null,
    val participantIds: List<String> = emptyList(),
    val lastMessage: String? = null
)