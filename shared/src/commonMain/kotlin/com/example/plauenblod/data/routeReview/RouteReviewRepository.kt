package com.example.plauenblod.data.routeReview

import com.example.plauenblod.model.RouteReview

interface RouteReviewRepository {

    suspend fun getReviewsForRoute(routeId: String): List<RouteReview>
    suspend fun addReview(routeId: String, review: RouteReview)
    suspend fun updateReview(routeId: String, updatedReview: RouteReview)
    suspend fun deleteReview(routeId: String, reviewId: String)
}