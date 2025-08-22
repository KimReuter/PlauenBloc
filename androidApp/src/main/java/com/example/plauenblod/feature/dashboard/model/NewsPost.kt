package com.example.plauenblod.feature.dashboard.model

data class NewsPost(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    val authorId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)