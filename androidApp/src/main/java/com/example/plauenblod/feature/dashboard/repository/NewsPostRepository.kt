package com.example.plauenblod.feature.dashboard.repository

import com.example.plauenblod.feature.dashboard.model.NewsPost
import kotlinx.coroutines.flow.Flow

interface NewsPostRepository {
    fun observeNews(): Flow<List<NewsPost>>
    suspend fun createPost(post: NewsPost): String
    suspend fun updatePost(post: NewsPost)
    suspend fun deletePost(postId: String)
    suspend fun getPostById(postId: String): NewsPost?
}