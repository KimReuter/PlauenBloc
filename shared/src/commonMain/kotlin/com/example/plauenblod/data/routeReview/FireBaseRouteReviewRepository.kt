package com.example.plauenblod.data.routeReview

import com.example.plauenblod.model.RouteReview
import com.example.plauenblod.model.routeProperty.Difficulty
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class FireBaseRouteReviewRepository: RouteReviewRepository {

    override suspend fun addReview(review: RouteReview) {
        try {
            val reviewData = mapOf(
                "userId" to review.userId,
                "stars" to review.stars,
                "comment" to review.comment,
                "completed" to review.completed,
                "completionDate" to review.completionDate,
                "attempts" to review.attempts,
                "perceivedDifficulty" to review.perceivedDifficulty?.name,
                "timeStamp" to review.timeStamp.toEpochMilliseconds(),
                "routeId" to review.routeId
            )

            Firebase.firestore
                .collection("routeReviews")
                .add(reviewData)
        } catch (e: Exception) {
            println("Fehler beim Hinzufügen der Bewertung: ${e.message}")
        }
    }

    override suspend fun getReviewsForRoute(routeId: String): List<RouteReview> {
        return try {
            val snapshot = Firebase.firestore
                .collection("routeReviews")
                .where("routeId", "==", routeId)
                .get()
                .documents

            snapshot.mapNotNull { doc ->
                try {
                    RouteReview(
                        userId = doc.get("userId") as? String ?: return@mapNotNull null,
                        stars = (doc.get("stars") as? Long)?.toInt() ?: return@mapNotNull null,
                        comment = doc.get("comment") as? String ?: "",
                        completed = doc.get("completed") as? Boolean ?: false,
                        completionDate = doc.get("completionDate") as? String,
                        attempts = (doc.get("attempts") as? Long)?.toInt() ?: return@mapNotNull null,
                        perceivedDifficulty = (doc.get("perceivedDifficulty") as? String)?.let { Difficulty.valueOf(it) } ?: return@mapNotNull null,
                        timeStamp = (doc.get("timeStamp") as? Long)
                            ?.let { Instant.fromEpochMilliseconds(it) }
                            ?: Clock.System.now()
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}