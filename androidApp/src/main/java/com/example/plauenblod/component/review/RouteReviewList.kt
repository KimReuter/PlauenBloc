package com.example.plauenblod.component.review

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.plauenblod.model.RouteReview

@Composable
fun RouteReviewList(
    reviews: List<RouteReview>
) {
    LazyColumn {
        items (reviews) { review ->
            ReviewItem(review)
        }
    }
}