package com.example.plauenblod.component.review

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import com.example.plauenblod.component.navigation.TabItem
import com.example.plauenblod.feature.routeReview.model.RouteReview
import com.example.plauenblod.screen.DashboardRoute
import com.example.plauenblod.screen.OwnProfileRoute
import com.example.plauenblod.screen.UserProfileRoute

fun LazyListScope.ReviewListSection(
    reviewsExpanded: Boolean,
    reviews: List<RouteReview>,
    currentUserId: String,
    onEdit: (RouteReview) -> Unit,
    onDelete: (RouteReview) -> Unit,
    onUserClick: (String) -> Unit,
    navController: NavController,
    selectedTab: MutableState<TabItem>
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
                    onDelete = { onDelete(it) },
                    onUserClick = { userId ->
                        if (userId == currentUserId) {
                            selectedTab.value = TabItem.USER
                            navController.navigate(OwnProfileRoute) {
                                launchSingleTop = true
                                popUpTo(DashboardRoute) { inclusive = false }
                            }
                        } else {
                            selectedTab.value = TabItem.COMMUNITY
                            navController.navigate(UserProfileRoute(userId)) {
                                launchSingleTop = true
                                popUpTo(DashboardRoute) { inclusive = false }
                            }
                        }
                    }
                )
            }
        }
    }
}