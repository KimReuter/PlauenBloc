package com.example.plauenblod.feature.communityPost.repository

import android.util.Log
import com.example.plauenblod.feature.communityPost.model.CommunityPost
import com.example.plauenblod.feature.communityPost.model.PostComment
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PinBoardRepositoryImpl(
) : PinBoardRepository {
    private val db = Firebase.firestore
    private val postsCollection = db.collection("community_posts")

    override fun getAllPosts(): Flow<List<CommunityPost>> = callbackFlow {
        val listener = postsCollection
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                Log.d("PinBoardRepo", "⚡️ onSnapshot: ${snapshot?.documents?.size} posts")
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val posts = snapshot.documents.mapNotNull { doc ->
                    val post = doc.toObject(CommunityPost::class.java) ?: return@mapNotNull null
                    post.id = doc.id
                    Log.d("PinBoardRepo", "→ Post ${post.id}: authorName='${post.authorName}'")
                    post
                }
                trySend(posts)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun createPost(post: CommunityPost) {
        postsCollection.add(post).await()
    }

    override suspend fun updatePost(postId: String, newContent: String) {
        db.collection("community_posts")
            .document(postId)
            .update("content", newContent)
            .await()
    }

    override suspend fun deletePost(postId: String) {
        db.collection("community_posts")
            .document(postId)
            .delete()
            .await()
    }

    override suspend fun addComment(postId: String, comment: PostComment) {
        val postRef = postsCollection.document(postId)
        val commentWithId = comment.apply {
            if (id.isEmpty()) id = UUID.randomUUID().toString()
        }
        postRef.update("comments", FieldValue.arrayUnion(commentWithId)).await()
    }


    override suspend fun updateComment(postId: String, commentId: String, newContent: String) {
        val postRef = db.collection("community_posts").document(postId)
        val snapshot = postRef.get().await()
        val post = snapshot.toObject(CommunityPost::class.java) ?: return
        val updatedComments = post.comments.map { c ->
            if (c.id == commentId) {
                PostComment().apply {
                    id = c.id
                    authorId = c.authorId
                    authorName = c.authorName
                    authorImageUrl = c.authorImageUrl
                    content = newContent
                    timestamp = c.timestamp
                }
            } else c
        }
        postRef.update("comments", updatedComments).await()
    }

    override suspend fun deleteComment(postId: String, commentId: String) {
        Log.d("PinBoardRepo", "→ deleteComment: removing $commentId from post $postId")
        val postRef = db.collection("community_posts").document(postId)
        val snapshot = postRef.get().await()
        val post = snapshot.toObject(CommunityPost::class.java) ?: return
        val updatedComments = post.comments.filter { it.id != commentId }
        postRef.update("comments", updatedComments).await()
    }

    override suspend fun addReaction(postId: String, emoji: String) {
        postsCollection
            .document(postId)
            .update("reactions.$emoji", FieldValue.increment(1))
            .await()
    }

    override suspend fun removeReaction(postId: String, emoji: String) {
        postsCollection
            .document(postId)
            .update("reactions.$emoji", FieldValue.increment(-1))
            .await()
    }
}