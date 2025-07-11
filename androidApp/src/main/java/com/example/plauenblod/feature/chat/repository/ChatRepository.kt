package com.example.plauenblod.feature.chat.repository

import com.example.plauenblod.feature.chat.model.Chat
import com.example.plauenblod.feature.chat.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    suspend fun getOrCreateChat(user1: String, user2: String): Chat
    suspend fun sendMessage(chatId: String, message: String, senderId: String, recipientId: String, routeId: String? = null)
    suspend fun observeMessages(chatId: String): Flow<List<Message>>
    suspend fun updateMessageReaction(messageId: String, chatId: String, userId: String, emoji: String)
    suspend fun deleteMessage(chatId: String, messageId: String): Result<Unit>
    suspend fun updateMessage(chatId: String, messageId: String, newContent: String): Result<Unit>
}