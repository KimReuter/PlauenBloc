package com.example.plauenblod.feature.communityPost.model

import com.example.plauenblod.android.util.FirestoreInstant

data class PostComment(
    val id: String = "",
    val authorId: String,
    val authorName: String,
    val content: String,
    val timestamp: FirestoreInstant
)