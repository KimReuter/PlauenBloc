package com.example.plauenblod.feature.chat.model

import com.example.plauenblod.feature.user.model.UserDto

data class ChatWithUser(
    val chat: Chat,
    val otherUser: UserDto
)