package com.example.plauenblod.feature.communityPost.model

import com.example.plauenblod.android.util.FirestoreInstant

data class CommunityPost(
    val id: String = "",
    val authorId: String,
    val authorName: String,
    val authorImageUrl: String?,
    val content: String,
    val timestamp: FirestoreInstant,
    val comments: List<PostComment> = emptyList()
)