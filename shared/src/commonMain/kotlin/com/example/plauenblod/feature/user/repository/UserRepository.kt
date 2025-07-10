package com.example.plauenblod.feature.user.repository

import com.example.plauenblod.feature.route.model.Route
import com.example.plauenblod.feature.user.model.UserActivity
import com.example.plauenblod.feature.user.model.UserDto
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun getUserById(userId: String): Result<UserDto>
    fun getAllUsers(): Flow<List<UserDto>>
    suspend fun tickRoute(userId: String, route: Route, Attempts: Int, isFlash: Boolean): Result<Unit>
}