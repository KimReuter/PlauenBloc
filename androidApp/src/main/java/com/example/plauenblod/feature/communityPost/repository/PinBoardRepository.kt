package com.example.plauenblod.feature.communityPost.repository

import com.example.plauenblod.feature.communityPost.model.CommunityPost
import com.example.plauenblod.feature.communityPost.model.PostComment
import kotlinx.coroutines.flow.Flow

interface PinBoardRepository {
    fun getAllPosts(): Flow<List<CommunityPost>>
    suspend fun createPost(post: CommunityPost)
    suspend fun updatePost(postId: String, newContent: String)
    suspend fun deletePost(postId: String)
    suspend fun addComment(postId: String, comment: PostComment)
    suspend fun updateComment(postId: String, commentId: String, newContent: String)
    suspend fun deleteComment(postId: String, commentId: String)
    fun observeComments(postId: String): Flow<List<PostComment>>
    suspend fun addReaction(postId: String, emoji: String)
    suspend fun removeReaction(postId: String, emoji: String)
}