package com.example.plauenblod.feature.user.repository


import com.example.plauenblod.feature.route.model.Route
import com.example.plauenblod.feature.route.model.util.calculatePoints
import com.example.plauenblod.feature.user.model.CompletedRoute
import com.example.plauenblod.feature.user.model.UserDto
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirebaseUserRepository : UserRepository {

    private val db = Firebase.firestore
    private val collection = db.collection("users")

    override suspend fun getUserById(userId: String): Result<UserDto> = runCatching {
        val userSnapshot = collection
            .document(userId)
            .get()
            .await()

        val user = userSnapshot.toObject(UserDto::class.java)
            ?: throw Exception("User nicht gefunden")

        val completedRoutesSnapshot = collection
            .document(userId)
            .collection("completedRoutes")
            .get()
            .await()

        val completedRoutes = completedRoutesSnapshot.documents.mapNotNull { doc ->
            doc.toObject(CompletedRoute::class.java)
        }

        user.copy(completedRoutes = completedRoutes)
    }

    override suspend fun tickRoute(
        userId: String,
        route: Route,
        attempts: Int,
        isFlash: Boolean
    ): Result<Unit> = runCatching {
        val userRef = collection.document(userId)

        val userSnapshot = userRef.get().await()
        val user = userSnapshot.toObject(UserDto::class.java)
            ?: throw Exception("User nicht gefunden")

        val currentPoints = user.totalPoints ?: 0
        val pointsToAdd = calculatePoints(route.difficulty, isFlash)
        val newTotalPoints = currentPoints + pointsToAdd

        userRef.update("totalPoints", newTotalPoints).await()

        userRef
            .collection("completedRoutes")
            .document(route.id)
            .set(
                mapOf(
                    "routeId" to route.id,
                    "routeName" to route.name,
                    "attempts" to attempts,
                    "difficulty" to route.difficulty,
                    "date" to System.currentTimeMillis().toString()
                )
            )
            .await()
    }
}