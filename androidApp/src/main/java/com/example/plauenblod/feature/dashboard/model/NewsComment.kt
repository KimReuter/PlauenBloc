package com.example.plauenblod.feature.dashboard.model

import kotlinx.serialization.Serializable

@Serializable
data class NewsComment(
    val id: String = "",
    val postId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorImageUrl: String? = null,
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)