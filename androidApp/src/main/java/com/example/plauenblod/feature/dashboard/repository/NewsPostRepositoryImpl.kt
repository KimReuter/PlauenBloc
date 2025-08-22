package com.example.plauenblod.feature.dashboard.repository

import com.example.plauenblod.feature.dashboard.model.NewsPost
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NewsPostRepositoryImpl(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
): NewsPostRepository {
    private val col = db.collection("dashboard_posts")

    override fun observeNews(): Flow<List<NewsPost>> = callbackFlow {
        val sub = col.orderBy("timestamp").addSnapshotListener { snap, e ->
            if (e != null) { close(e); return@addSnapshotListener }
            val list = snap!!.documents.mapNotNull { it.toObject(NewsPost::class.java)?.copy(id = it.id) }
            trySend(list)
        }
        awaitClose { sub.remove() }
    }

    override suspend fun updatePost(post: NewsPost) {
        col.document(post.id)
            .set(post)
            .await()
    }

    override suspend fun deletePost(postId: String) {
        col.document(postId).delete().await()
    }

    override suspend fun createPost(post: NewsPost): String {
        val ref = col.add(post.copy(id = "")).await()
        return ref.id
    }

    override suspend fun getPostById(postId: String): NewsPost? {
        val snap = col.document(postId).get().await()
        return snap.toObject(NewsPost::class.java)
            ?.copy(id = snap.id)
    }
}