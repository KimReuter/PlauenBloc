package com.example.plauenblod.feature.ranking.repository

import com.example.plauenblod.feature.route.model.Route
import com.example.plauenblod.feature.user.model.UserDto
import kotlinx.coroutines.flow.Flow

interface LeaderboardRepository {
    fun observeUsers(): Flow<List<UserDto>>
    fun observeAllRoutes(): Flow<List<Route>>
}