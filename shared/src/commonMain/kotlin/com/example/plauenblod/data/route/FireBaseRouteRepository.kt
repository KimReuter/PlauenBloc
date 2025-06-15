package com.example.plauenblod.data.route

import com.example.plauenblod.model.Route
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class FireBaseRouteRepository: RouteRepository {

    override suspend fun createRoute(route: Route): Result<Unit> {
        return try {
            val routeMap = mapOf(
                "name" to route.name,
                "holdColor" to route.holdColor,
                "difficulty" to route.difficulty,
                "number" to route.number,
                "description" to route.description,
                "setter" to route.setter,
                "x" to route.x,
                "y" to route.y
            )
            Firebase.firestore.collection("routes").add(routeMap)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun editRoute(routeId: String, updatedRoute: Route): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRoute(routeId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllRoutes(): List<Route> {
        TODO("Not yet implemented")
    }
}