package com.example.plauenblod.data.route

import com.example.plauenblod.model.Route

interface RouteRepository {

    suspend fun createRoute(route: Route): Result<Unit>
    suspend fun editRoute(routeId: String, updatedRoute: Route): Result<Unit>
    suspend fun deleteRoute(routeId: String): Result<Unit>
    suspend fun getAllRoutes(): List<Route>
}