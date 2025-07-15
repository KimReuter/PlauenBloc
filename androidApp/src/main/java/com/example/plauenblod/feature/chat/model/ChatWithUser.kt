package com.example.plauenblod.feature.chat.model

import com.example.plauenblod.feature.user.model.UserDto
import kotlinx.datetime.Instant

data class ChatWithUser (
    val chat: Chat,
    val otherUser: UserDto,
) {
    val lastMessageTimestamp: Instant?
        get() = chat.lastMessageTimestamp?.toInstant()
}