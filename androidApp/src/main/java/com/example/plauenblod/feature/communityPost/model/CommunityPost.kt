package com.example.plauenblod.feature.communityPost.model

import com.example.plauenblod.android.util.FirestoreInstant
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class CommunityPost(
    var id: String               = "",
    var authorId: String         = "",
    var authorName: String       = "",
    var authorImageUrl: String?  = null,
    var content: String          = "",
    var timestamp: FirestoreInstant = FirestoreInstant(),
    var comments: List<PostComment> = emptyList(),
    var reactions: Map<String, Int> = emptyMap(),
    var likeCount: Int = 0,
    var repostCount: Int = 0,
    var commentCount: Int = 0
) {
    constructor() : this("", "", "", null, "", FirestoreInstant(), emptyList())
}