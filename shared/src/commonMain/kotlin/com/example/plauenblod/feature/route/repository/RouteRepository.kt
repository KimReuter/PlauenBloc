package com.example.plauenblod.feature.route.repository

import com.example.plauenblod.feature.route.model.Route
import kotlinx.coroutines.flow.Flow

interface RouteRepository {

    suspend fun createRoute(route: Route): Result<Unit>
    suspend fun editRoute(routeId: String, updatedRoute: Route): Result<Unit>
    suspend fun deleteRoute(routeId: String): Result<Unit>
    suspend fun getAllRoutes(): List<Route>
    suspend fun getRouteById(routeId: String): Route?
}