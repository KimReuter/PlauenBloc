package com.example.plauenblod.feature.chat.model

import com.example.plauenblod.android.util.FirestoreInstant

data class Chat (
    val id: String? = null,
    val participantIds: List<String> = emptyList(),
    val lastMessageTimestamp: FirestoreInstant? = null,
    val lastMessage: String? = null
) {
    fun getLastMessageInstant() = lastMessageTimestamp?.toInstant()
}