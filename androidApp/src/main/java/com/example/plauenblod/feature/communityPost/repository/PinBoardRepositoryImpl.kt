package com.example.plauenblod.feature.communityPost.repository

import com.example.plauenblod.feature.communityPost.model.CommunityPost
import com.example.plauenblod.feature.communityPost.model.PostComment
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PinBoardRepositoryImpl(
) : PinBoardRepository {
    private val db =  Firebase.firestore
    private val postsCollection = db.collection("community_posts")

    override fun getAllPosts(): Flow<List<CommunityPost>> = callbackFlow {
        val listener = postsCollection
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val posts = snapshot.documents.mapNotNull { it.toObject<CommunityPost>()?.copy(id = it.id) }
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
        postRef.update("comments", com.google.firebase.firestore.FieldValue.arrayUnion(comment)).await()
    }


    override suspend fun updateComment(postId: String, commentId: String, newContent: String) {
        val postRef = db.collection("community_posts").document(postId)
        val snapshot = postRef.get().await()
        val post = snapshot.toObject(CommunityPost::class.java) ?: return
        val updatedComments = post.comments.map {
            if (it.id == commentId) it.copy(content = newContent) else it
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