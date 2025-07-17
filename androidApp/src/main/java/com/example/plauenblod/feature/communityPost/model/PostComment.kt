package com.example.plauenblod.feature.communityPost.model

import com.example.plauenblod.android.util.FirestoreInstant
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class PostComment {
    var id: String = ""
    var authorId: String = ""
    var authorName: String = ""
    var authorImageUrl: String? = null
    var content: String = ""
    var timestamp: FirestoreInstant = FirestoreInstant()
}