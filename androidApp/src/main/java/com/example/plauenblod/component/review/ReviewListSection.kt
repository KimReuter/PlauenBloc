package com.example.plauenblod.component.review

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.example.plauenblod.feature.routeReview.model.RouteReview

fun LazyListScope.ReviewListSection(
    reviewsExpanded: Boolean,
    reviews: List<RouteReview>,
    currentUserId: String,
    onEdit: (RouteReview) -> Unit,
    onDelete: (RouteReview) -> Unit
) {
    if (reviewsExpanded) {
        if (reviews.isEmpty()) {
            item {
                Text(
                    text = "Noch keine Rezensionen vorhanden.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        } else {
            itemsIndexed(reviews) { index, review ->
                ReviewItem(
                    review,
                    backgroundColor = if (index % 2 == 0)
                        MaterialTheme.colorScheme.surface
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    currentUserId = currentUserId,
                    onEdit = { onEdit(it) },
                    onDelete = { onDelete(it) }
                )
            }
        }
    }
}