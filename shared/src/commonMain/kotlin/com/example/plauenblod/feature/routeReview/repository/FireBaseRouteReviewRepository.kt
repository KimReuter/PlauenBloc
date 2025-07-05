package com.example.plauenblod.feature.routeReview.repository

import com.example.plauenblod.feature.route.model.routeProperty.Difficulty
import com.example.plauenblod.feature.routeReview.repository.RouteReviewRepository
import com.example.plauenblod.feature.routeReview.model.RouteReview
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class FireBaseRouteReviewRepository: RouteReviewRepository {

    override suspend fun addReview(routeId: String, review: RouteReview) {
        try {
            Firebase.firestore
                .collection("routes")
                .document(routeId)
                .collection("reviews")
                .add(review)
        } catch (e: Exception) {
            println("Fehler beim Hinzufügen der Bewertung: ${e.message}")
        }
    }

    override suspend fun getReviewsForRoute(routeId: String): List<RouteReview> {
        return try {
            val snapshot = Firebase.firestore
                .collection("routes")
                .document(routeId)
                .collection("reviews")
                .get()
                .documents

            snapshot.mapNotNull { doc ->
                try {
                    RouteReview(
                        id = doc.id,
                        routeId = routeId,
                        userId = doc.get("userId") as? String ?: return@mapNotNull null,
                        userName = doc.get("userName") as? String ?: "Anonymer Nutzer",
                        userProfileImageUrl = doc.get("userProfileImageUrl") as? String,
                        stars = (doc.get("stars") as? Long)?.toInt() ?: 0,
                        comment = doc.get("comment") as? String ?: "",
                        completed = doc.get("completed") as? Boolean ?: false,
                        completionDate = doc.get("completionDate") as? String,
                        attempts = (doc.get("attempts") as? Long)?.toInt() ?: 1,
                        perceivedDifficulty = (doc.get("perceivedDifficulty") as? String)
                            ?.let { Difficulty.valueOf(it) },
                        timeStamp = (doc.get("timeStamp") as? String)
                            ?.let { Instant.Companion.parse(it) }
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

    override suspend fun updateReview(routeId: String, updatedReview: RouteReview) {
        try {
            Firebase.firestore
                .collection("routes")
                .document(routeId)
                .collection("reviews")
                .document(updatedReview.id)
                .set(updatedReview)
        } catch (e: Exception) {
            println("Fehler beim Aktualisieren der Bewertung: ${e.message}")
        }
    }

    override suspend fun deleteReview(routeId: String, reviewId: String) {
        try {
            Firebase.firestore
                .collection("routes")
                .document(routeId)
                .collection("reviews")
                .document(reviewId)
                .delete()
        } catch (e: Exception) {
            println("Fehler beim Löschen der Bewertung: ${e.message}")
        }
    }
}