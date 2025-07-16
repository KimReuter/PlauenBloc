package com.example.plauenblod.feature.communityPost.repository

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
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val posts = snapshot.documents.mapNotNull { doc ->
                    val post = doc.toObject(CommunityPost::class.java) ?: return@mapNotNull null
                    post.id = doc.id

                    post.comments.forEachIndexed { index, comment ->
                        if (comment.id.isEmpty()) {
                            comment.id = UUID.randomUUID().toString()
                        }
                    }

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
                    content = newContent
                    timestamp = c.timestamp
                }
            } else c
        }
        postRef.update("comments", updatedComments).await()
    }

    override suspend fun deleteComment(postId: String, commentId: String) {
        val postRef = db.collection("community_posts").document(postId)
        val snapshot = postRef.get().await()
        val post = snapshot.toObject(CommunityPost::class.java) ?: return
        val updatedComments = post.comments.filter { it.id != commentId }
        postRef.update("comments", updatedComments).await()
    }
}