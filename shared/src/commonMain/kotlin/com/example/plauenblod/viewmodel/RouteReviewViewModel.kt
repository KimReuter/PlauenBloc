package com.example.plauenblod.viewmodel

import com.example.plauenblod.data.routeReview.RouteReviewRepository
import com.example.plauenblod.model.RouteReview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RouteReviewViewModel(
    private val repository: RouteReviewRepository,
    private val coroutineScope: CoroutineScope
) {
    private val _reviews = MutableStateFlow<List<RouteReview>>(emptyList())
    val reviews: StateFlow<List<RouteReview>> = _reviews

    private val _hasUserReviewed = MutableStateFlow(false)
    val hasUserReviewed: StateFlow<Boolean> = _hasUserReviewed

    fun loadReviews(routeId: String, currentUserId: String) {
        coroutineScope.launch {
            val loaded = repository.getReviewsForRoute(routeId)
            _reviews.value = loaded
            _hasUserReviewed.value = loaded.any { it.userId == currentUserId }
        }
    }

    fun addReview(routeId: String, review: RouteReview, onResult: (Boolean) -> Unit) {
        coroutineScope.launch {
            try {
                repository.addReview(routeId, review)
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun updateReview(routeId: String, updatedReview: RouteReview, onResult: (Boolean) -> Unit) {
        coroutineScope.launch {
            try {
                repository.updateReview(routeId, updatedReview)
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun deleteReview(routeId: String, reviewId: String, onResult: (Boolean) -> Unit) {
        coroutineScope.launch {
            try {
                repository.deleteReview(routeId, reviewId)
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

}