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

    fun loadReviews(routeId: String) {
        coroutineScope.launch {
            val loaded = repository.getReviewsForRoute(routeId)
            _reviews.value = loaded
        }
    }

    fun addReview(review: RouteReview, onResult: (Boolean) -> Unit) {
        coroutineScope.launch {
            try {
                repository.addReview(review)
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

}