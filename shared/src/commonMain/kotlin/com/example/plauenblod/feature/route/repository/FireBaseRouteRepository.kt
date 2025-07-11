package com.example.plauenblod.feature.route.repository

import com.example.plauenblod.feature.route.model.Route
import com.example.plauenblod.feature.route.model.routeProperty.Difficulty
import com.example.plauenblod.feature.route.model.routeProperty.HallSection
import com.example.plauenblod.feature.route.model.routeProperty.HoldColor
import com.example.plauenblod.feature.route.model.routeProperty.Sector
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class FireBaseRouteRepository: RouteRepository {

    val db = Firebase.firestore

    override suspend fun createRoute(route: Route): Result<Unit> {
        return try {
            val routeMap = mapOf(
                "name" to route.name,
                "hall" to route.hall.name,
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

            db.collection("routes").add(routeMap)
            println("✅ FirebaseRepo → createRoute(): Erfolgreich hinzugefügt!")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ FirebaseRepo → createRoute(): Fehler: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun editRoute(routeId: String, updatedRoute: Route): Result<Unit> {
        return try {
            println("✏️ editRoute: Versuche Route $routeId zu aktualisieren.")
            val routeMap = mapOf(
                "name" to updatedRoute.name,
                "hall" to updatedRoute.hall.name,
                "sector" to updatedRoute.sector.name,
                "holdColor" to updatedRoute.holdColor.name,
                "difficulty" to updatedRoute.difficulty.name,
                "number" to updatedRoute.number,
                "description" to updatedRoute.description,
                "setter" to updatedRoute.setter,
                "x" to updatedRoute.x,
                "y" to updatedRoute.y,
                "points" to updatedRoute.points
            )
            db.collection("routes")
                .document(routeId)
                .set(routeMap)
            println("✅ Route $routeId erfolgreich aktualisiert.")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ Fehler beim Aktualisieren: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteRoute(routeId: String): Result<Unit> {
        return try {
            db.collection("routes").document(routeId).delete()
            println("🗑️ FirebaseRepo -> deleteRoute(): Route $routeId gelöscht.")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ FirebaseRepo → deleteRoute(): Fehler: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getAllRoutes(): List<Route> {
        return try {
            val snapshot = db.collection("routes").get().documents

            snapshot.mapNotNull { doc ->
                try {
                    Route(
                        id = doc.id,
                        name = doc.get("name") as? String ?: return@mapNotNull null,
                        hall = (doc.get("hall") as? String)?.let { HallSection.valueOf(it) } ?: return@mapNotNull null,
                        sector = (doc.get("sector") as? String)?.let { Sector.valueOf(it) } ?: return@mapNotNull null,
                        holdColor = (doc.get("holdColor") as? String)?.let { HoldColor.valueOf(it) } ?: return@mapNotNull null,
                        difficulty = (doc.get("difficulty") as? String)?.let { Difficulty.valueOf(it) } ?: return@mapNotNull null,
                        number = (doc.get("number") as? Long)?.toInt() ?: return@mapNotNull null,
                        description = doc.get("description") as? String ?: "",
                        setter = doc.get("setter") as? String ?: "",
                        x = (doc.get("x") as? Double)?.toFloat() ?: return@mapNotNull null,
                        y = (doc.get("y") as? Double)?.toFloat() ?: return@mapNotNull null,
                        points = (doc.get("points") as? Long)?.toInt() ?: 0
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            println("❌ Fehler beim Abrufen der Routen: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getRouteById(routeId: String): Route? {
        return try {
            val doc = db.collection("routes").document(routeId).get()

            Route(
                id = doc.id,
                name = doc.get("name") as? String ?: return null,
                hall = (doc.get("hall") as? String)?.let { HallSection.valueOf(it) } ?: return null,
                sector = (doc.get("sector") as? String)?.let { Sector.valueOf(it) } ?: return null,
                holdColor = (doc.get("holdColor") as? String)?.let { HoldColor.valueOf(it) } ?: return null,
                difficulty = (doc.get("difficulty") as? String)?.let { Difficulty.valueOf(it) } ?: return null,
                number = (doc.get("number") as? Long)?.toInt() ?: return null,
                description = doc.get("description") as? String ?: "",
                setter = doc.get("setter") as? String ?: "",
                x = (doc.get("x") as? Double)?.toFloat() ?: return null,
                y = (doc.get("y") as? Double)?.toFloat() ?: return null,
                points = (doc.get("points") as? Long)?.toInt() ?: 0
            )
        } catch (e: Exception) {
            println("❌ Fehler in getRouteById: ${e.message}")
            null
        }
    }
}