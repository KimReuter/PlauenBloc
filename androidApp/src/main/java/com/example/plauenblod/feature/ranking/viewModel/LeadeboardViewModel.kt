package com.example.plauenblod.feature.ranking.viewModel

import com.example.plauenblod.feature.dashboard.model.LeaderboardEntry
import com.example.plauenblod.feature.ranking.repository.LeaderboardRepository
import com.example.plauenblod.feature.route.model.util.calculatePoints
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class LeaderboardViewModel(
    private val repo: LeaderboardRepository,
    coroutineScope: CoroutineScope
) {
    private val usersFlow  = repo.observeUsers()
    private val routesFlow = repo.observeAllRoutes()

    val leaderboardState: StateFlow<LeaderboardUiState> =
        combine(usersFlow, routesFlow) { users, routes ->

            val maxPoints = routes.sumOf { calculatePoints(it.difficulty, isFlash = false).toLong() }

            val entries = users.mapIndexed { index, user ->
                LeaderboardEntry(
                    userId = user.uid ?: "",
                    userName = user.userName ?: "Unbekannt",
                    profileImageUrl = user.profileImageUrl,
                    points = user.totalPoints ?: 0L,
                    routeCount = user.completedRoutes.size,
                    rank = 0
                )
            }
                .sortedByDescending { it.points }
                .mapIndexed { idx, entry -> entry.copy(rank = idx + 1) }

                .sortedByDescending { it.points }
                .mapIndexed { index, entry -> entry.copy(rank = index + 1) }

            LeaderboardUiState(
                maxPoints = maxPoints,
                entries = entries
            )
        }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = LeaderboardUiState(0L, emptyList())
            )
}

data class LeaderboardUiState(
    val maxPoints: Long,
    val entries:   List<LeaderboardEntry>
)