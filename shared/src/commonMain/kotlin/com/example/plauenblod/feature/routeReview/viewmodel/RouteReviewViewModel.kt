package com.example.plauenblod.feature.routeReview.viewmodel

import com.example.plauenblod.feature.auth.repository.AuthRepository
import com.example.plauenblod.feature.route.model.routeProperty.Difficulty
import com.example.plauenblod.feature.routeReview.model.RouteReview
import com.example.plauenblod.feature.routeReview.repository.RouteReviewRepository
import com.example.plauenblod.feature.user.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RouteReviewViewModel(
    private val routeRepository: RouteReviewRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val coroutineScope: CoroutineScope
) {
    private val _reviews = MutableStateFlow<List<RouteReview>>(emptyList())
    val reviews: StateFlow<List<RouteReview>> = _reviews

    private val _hasUserReviewed = MutableStateFlow(false)
    val hasUserReviewed: StateFlow<Boolean> = _hasUserReviewed

    fun loadReviews(routeId: String, currentUserId: String) {
        coroutineScope.launch {
            val loaded = routeRepository.getReviewsForRoute(routeId)
            _reviews.value = loaded
            _hasUserReviewed.value = loaded.any { it.userId == currentUserId }
        }
    }

    fun addReview(
        routeId: String,
        stars: Int,
        comment: String,
        completed: Boolean,
        attempts: Int,
        perceivedDifficulty: Difficulty,
        onResult: (Boolean) -> Unit
    ) {
        coroutineScope.launch {
            try {
                val currentUserId = authRepository.getCurrentUserId()
                val result = userRepository.getUserById(currentUserId ?: return@launch)
                val user = result.getOrNull()

                if (user != null) {
                    println("👤 Nutzer geladen: ${user.userName}, Bild: ${user.profileImageUrl}")

                    val review = RouteReview(
                        id = "",
                        routeId = routeId,
                        userId = user.uid ?: return@launch,
                        userName = user.userName ?: "Unbekannter Nutzer",
                        userProfileImageUrl = user.profileImageUrl,
                        stars = stars,
                        comment = comment,
                        completed = completed,
                        attempts = attempts,
                        perceivedDifficulty = perceivedDifficulty,
                        timeStamp = kotlinx.datetime.Clock.System.now()
                    )

                    routeRepository.addReview(routeId, review)
                    onResult(true)
                } else {
                    println("❌ Nutzer konnte nicht geladen werden.")
                    onResult(false)
                }
            } catch (e: Exception) {
                println("⚠️ Fehler beim Erstellen des Reviews: ${e.message}")
                onResult(false)
            }
        }
    }

    fun updateReview(routeId: String, updatedReview: RouteReview, onResult: (Boolean) -> Unit) {
        coroutineScope.launch {
            try {
                routeRepository.updateReview(routeId, updatedReview)
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun deleteReview(routeId: String, reviewId: String, onResult: (Boolean) -> Unit) {
        coroutineScope.launch {
            try {
                routeRepository.deleteReview(routeId, reviewId)
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

}