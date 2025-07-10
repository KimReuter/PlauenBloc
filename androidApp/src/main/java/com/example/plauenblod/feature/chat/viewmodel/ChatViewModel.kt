package com.example.plauenblod.feature.chat.viewmodel

import com.example.plauenblod.feature.chat.model.Chat
import com.example.plauenblod.feature.chat.model.Message
import com.example.plauenblod.feature.chat.repository.ChatRepository
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val coroutineScope: CoroutineScope
) {
    private val _chat = MutableStateFlow<Chat?>(null)
    val chat: StateFlow<Chat?> = _chat.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _selectedMessage = MutableStateFlow<Message?>(null)
    val selectedMessage = _selectedMessage.asStateFlow()

    private var messageJob: Job? = null
    private var currentUserId: String? = null

    fun initChat(user1: String, user2: String) {
        println("initChat: Initialisiere Chat für $user1 und $user2")
        currentUserId = user1

        coroutineScope.launch {
            val createdChat = chatRepository.getOrCreateChat(user1, user2)
            println("initChat: Chat geladen mit ID ${createdChat.id}")
            _chat.value = createdChat

            messageJob?.cancel()
            messageJob = coroutineScope.launch {
                chatRepository.observeMessages(createdChat.id ?: return@launch)
                    .collect { newMessages ->
                        println("initChat: Neue Nachrichten empfangen: ${newMessages.size}")
                        _messages.value = newMessages
                    }
            }
        }
    }

    fun sendMessage(message: String, senderId: String) {
        val chatId = _chat.value?.id ?: return
        println("SendMessage wird ausgeführt")
        coroutineScope.launch {
            chatRepository.sendMessage(chatId, message, senderId)
        }
        println("Nachricht wurde gesendet: $message im Chat $chatId von $senderId")
    }

    fun reactToMessage(message: Message, emoji: String) {
        val chatId = _chat.value?.id ?: return
        val userId = currentUserId ?: return
        val messageId = message.id ?: return

        println("Reaktion wird gesendet: $emoji für Nachricht $messageId von $userId im Chat $chatId")

        coroutineScope.launch {
            chatRepository.updateMessageReaction(
                messageId = messageId,
                chatId = chatId,
                userId = userId,
                emoji = emoji
            )
        }
    }

    fun deleteMessage(messageId: String) {
        val chatId = _chat.value?.id ?: return

        coroutineScope.launch {
            val result = chatRepository.deleteMessage(chatId, messageId)
            result.onFailure {
                println("Fehler beim Löschen der Nachricht: ${it.message}")
            }
        }
    }

    fun updateMessage(messageId: String, newContent: String) {
        val chatId = _chat.value?.id ?: return

        coroutineScope.launch {
            val result = chatRepository.updateMessage(chatId, messageId, newContent)
            result.onFailure {
                println("Fehler beim Bearbeiten der Nachricht: ${it.message}")
            }
        }
    }

    fun selectMessage(message: Message) {
        _selectedMessage.value = message
    }

    fun clearSelectedMessage() {
        _selectedMessage.value = null
    }

}