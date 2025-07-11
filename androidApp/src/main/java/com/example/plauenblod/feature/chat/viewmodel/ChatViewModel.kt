package com.example.plauenblod.feature.chat.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import com.example.plauenblod.feature.chat.model.Chat
import com.example.plauenblod.feature.chat.model.Message
import com.example.plauenblod.feature.chat.repository.ChatRepository
import com.example.plauenblod.feature.route.model.Route
import com.example.plauenblod.feature.route.repository.RouteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val routeRepository: RouteRepository,
    private val coroutineScope: CoroutineScope
) {
    private val _chat = MutableStateFlow<Chat?>(null)
    val chat: StateFlow<Chat?> = _chat.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _selectedMessage = MutableStateFlow<Message?>(null)
    val selectedMessage = _selectedMessage.asStateFlow()

    private val _sharedRoutes = MutableStateFlow<Map<String, Route>>(emptyMap())
    val sharedRoutes: StateFlow<Map<String, Route>> = _sharedRoutes

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

                        newMessages.forEach { msg ->
                            msg.routeId?.let { routeId ->
                                loadRouteIfNeeded(routeId)
                            }
                        }
                    }
            }
        }
    }

    fun sendMessage(messageText: String, senderId: String, recipientId: String, routeId: String? = null) {
        val chatId = _chat.value?.id ?: return
        println("SendMessage wird ausgeführt")
        coroutineScope.launch {
            chatRepository.sendMessage(chatId, messageText, senderId, recipientId, routeId)
        }
        println("Nachricht wurde gesendet: $messageText im Chat $chatId von $senderId")
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

    fun loadRouteIfNeeded(routeId: String) {
        if (!sharedRoutes.value.containsKey(routeId)) {
            coroutineScope.launch {
                println("Versuche Route $routeId zu laden...")
                val route = routeRepository.getRouteById(routeId)
                route?.let {
                    _sharedRoutes.value = _sharedRoutes.value + (routeId to it)
                    println("Route $routeId geladen: ${it.name}")
                } ?: println("Route $routeId konnte nicht gefunden werden.")
            }
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