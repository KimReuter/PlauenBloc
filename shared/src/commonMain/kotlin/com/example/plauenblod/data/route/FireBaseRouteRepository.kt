package com.example.plauenblod.data.route

import com.example.plauenblod.model.Difficulty
import com.example.plauenblod.model.HoldColor
import com.example.plauenblod.model.Route
import com.example.plauenblod.model.Sector
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class FireBaseRouteRepository: RouteRepository {

    override suspend fun createRoute(route: Route): Result<Unit> {
        return try {
            val routeMap = mapOf(
                "name" to route.name,
                "sector" to route.sector.name,
                "holdColor" to route.holdColor.name,
                "difficulty" to route.difficulty.name,
                "number" to route.number,
                "description" to route.description,
                "setter" to route.setter,
                "x" to route.x,
                "y" to route.y
            )
            println("📤 FirebaseRepo → createRoute(): Sende Route an Firestore:")

            Firebase.firestore.collection("routes").add(routeMap)
            println("✅ FirebaseRepo → createRoute(): Erfolgreich hinzugefügt!")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ FirebaseRepo → createRoute(): Fehler: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun editRoute(routeId: String, updatedRoute: Route): Result<Unit> {
        return try {
            val routeMap = mapOf(
                "name" to updatedRoute.name,
                "sector" to updatedRoute.sector,
                "holdColor" to updatedRoute.holdColor.name,
                "difficulty" to updatedRoute.difficulty.name,
                "number" to updatedRoute.number,
                "description" to updatedRoute.description,
                "setter" to updatedRoute.setter,
                "x" to updatedRoute.x,
                "y" to updatedRoute.y
            )
            Firebase.firestore.collection("routes").document(routeId).set(routeMap)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteRoute(routeId: String): Result<Unit> {
        return try {
            Firebase.firestore.collection("routes").document(routeId).delete()
            println("🗑️ FirebaseRepo -> deleteRoute(): Route $routeId gelöscht.")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ FirebaseRepo → deleteRoute(): Fehler: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getAllRoutes(): List<Route> {
        return try {
            val snapshot = Firebase.firestore.collection("routes").get().documents
            snapshot.mapNotNull { doc ->
                try {
                    Route(
                        id = doc.id,
                        name = doc.get("name") as? String ?: return@mapNotNull null,
                        sector = (doc.get("sector") as? String)?.let { Sector.valueOf(it)} ?: return@mapNotNull null,
                        holdColor = (doc.get("holdColor") as? String)?.let { HoldColor.valueOf(it) } ?: return@mapNotNull null,
                        difficulty = (doc.get("difficulty") as? String)?.let { Difficulty.valueOf(it) } ?: return@mapNotNull null,
                        number = (doc.get("number") as? Long)?.toInt() ?: return@mapNotNull null,
                        description = doc.get("description") as? String ?: "",
                        setter = doc.get("setter") as? String ?: "",
                        x = (doc.get("x") as? Double)?.toFloat() ?: return@mapNotNull null,
                        y = (doc.get("y") as? Double)?.toFloat() ?: return@mapNotNull null,
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}