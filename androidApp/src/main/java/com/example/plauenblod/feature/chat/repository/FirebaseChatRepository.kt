package com.example.plauenblod.feature.chat.repository

import android.util.Log
import com.example.plauenblod.feature.chat.model.Chat
import com.example.plauenblod.feature.chat.model.Message
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseChatRepository(

): ChatRepository {
    private val db = Firebase.firestore
    private val TAG = "FirebaseChatRepo"

    override suspend fun getOrCreateChat(user1: String, user2: String): Chat {
        Log.d(TAG, "getOrCreateChat: Suche nach bestehendem Chat für $user1 und $user2")

        val chatsRef = db.collection("chats")

        try {
            val existingChat = chatsRef
                .whereArrayContains("participantIds", user1)
                .get()
                .await()
                .mapNotNull {
                    val chat = it.toObject(Chat::class.java)?.copy(id = it.id)
                    chat
                }
                .firstOrNull { it.participantIds?.contains(user2) == true }

            if (existingChat != null) {
                Log.d(TAG, "getOrCreateChat: Bestehender Chat gefunden: ${existingChat.id}")
                return existingChat
            }

            val snapshot = chatsRef
                .whereArrayContains("participantIds", user1)
                .get()
                .await()

            Log.d(TAG, "RAW Firestore-Dokumente:")
            snapshot.documents.forEach {
                Log.d(TAG, "→ ${it.data}")
            }

            val chatDoc = chatsRef.document()
            val now: Long = System.currentTimeMillis()

            val newChatData = mapOf(
                "participantIds" to listOf(user1, user2),
                "lastMessage" to "",
                "lastTimeStamp" to now
            )

            chatDoc.set(newChatData).await()

            val newChat = Chat(
                id = chatDoc.id,
                participantIds = listOf(user1, user2),
                lastMessage = ""
            )

            Log.d(TAG, "getOrCreateChat: Neuer Chat erstellt mit ID: ${chatDoc.id}")
            return newChat

        } catch (e: Exception) {
            Log.e(TAG, "getOrCreateChat: Fehler beim Erstellen des Chats: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getChatsForUser(userId: String): List<Chat> {
        Log.d("FirebaseChatrepo", "Chats werden geladen")
        return try {
            val snapshot = Firebase.firestore
                .collection("chats")
                .whereArrayContains("participantIds", userId)
                .orderBy("lastTimeStamp", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Chat::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Abrufen der Chatliste: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun observeMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        Log.d(TAG, "observeMessages: Starte Listener für Chat $chatId")

        val listener = db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timeStamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.d(TAG, "observeMessages: Fehler beim Abrufen: ${error.message}")
                    return@addSnapshotListener
                }

                Log.d(
                    TAG,
                    "observeMessages: Snapshot erhalten mit ${snapshot?.documents?.size} Nachrichten"
                )

                val messages = snapshot?.documents?.mapNotNull {
                    val message = it.toObject(Message::class.java)?.copy(id = it.id)
                    if (message == null) println("observeMessages: Nachricht konnte nicht gemappt werden.")
                    message
                }

                trySend(messages ?: emptyList())
            }

        awaitClose {
            Log.d(TAG, "observeMessages: Listener für Chat $chatId entfernt")
            listener.remove()
        }
    }

    override suspend fun sendMessage(chatId: String, messageText: String, senderId: String, recipientId: String, routeId: String?) {
        Log.d(TAG, "sendMessage: Sende Nachricht '$messageText' von $senderId in Chat $chatId")

        val messagesRef = db.collection("chats").document(chatId).collection("messages")
        val now: Long = System.currentTimeMillis()

        val newMessageRef = messagesRef.document()
        val newMessage = Message(
            id = newMessageRef.id,
            senderId = senderId,
            recipientId = recipientId,
            messageText = messageText,
            chatId = chatId,
            timeStamp = now,
            routeId = routeId
        )

        newMessageRef.set(newMessage).await()
        Log.d(TAG, "sendMessage: Nachricht gespeichert")

        db.collection("chats").document(chatId).update(
            mapOf(
                "lastMessage" to messageText,
                "lastTimeStamp" to now
            )
        ).await()
        Log.d(TAG, "sendMessage: Chat-Dokument geupdatet")
    }

    override suspend fun updateMessageReaction(messageId: String, chatId: String, userId: String, emoji: String) {
        val messageRef = db
            .collection("chats")
            .document(chatId)
            .collection("messages")
            .document(messageId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(messageRef)
            val currentReactions = snapshot.get("reactions") as? Map<String, String> ?: emptyMap()
            val updatedReactions = currentReactions.toMutableMap().apply {
                this[userId] = emoji
            }

            transaction.update(messageRef, "reactions", updatedReactions)
        }
    }

    override suspend fun deleteMessage(chatId: String, messageId: String): Result<Unit> {
        return try {
            Firebase.firestore
                .collection("chats")
                .document(chatId)
                .collection("messages")
                .document(messageId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMessage(chatId: String, messageId: String, newContent: String): Result<Unit> {
        return try {
            Firebase.firestore
                .collection("chats")
                .document(chatId)
                .collection("messages")
                .document(messageId)
                .update("content", newContent)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}