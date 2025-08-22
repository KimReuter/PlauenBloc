package com.example.plauenblod.feature.routeReview.repository

import com.example.plauenblod.feature.routeReview.model.RouteReview

interface RouteReviewRepository {

    suspend fun getReviewsForRoute(routeId: String): List<RouteReview>
    suspend fun addReview(routeId: String, review: RouteReview)
    suspend fun updateReview(routeId: String, updatedReview: RouteReview)
    suspend fun deleteReview(routeId: String, reviewId: String)
}