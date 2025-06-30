package com.example.plauenblod.data.routeReview

import com.example.plauenblod.model.RouteReview

interface RouteReviewRepository {

    suspend fun getReviewsForRoute(routeId: String): List<RouteReview>
    suspend fun addReview(review: RouteReview)
}