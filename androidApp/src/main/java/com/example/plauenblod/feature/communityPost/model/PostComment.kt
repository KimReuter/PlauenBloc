package com.example.plauenblod.feature.communityPost.model

import com.example.plauenblod.android.util.FirestoreInstant
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class PostComment {
    @get: Exclude
    var id: String = ""
    var authorId: String = ""
    var authorName: String = ""
    var content: String = ""
    var timestamp: FirestoreInstant = FirestoreInstant()
}