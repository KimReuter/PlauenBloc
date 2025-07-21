package com.example.plauenblod.feature.ranking.repository

import com.example.plauenblod.feature.route.model.Route
import com.example.plauenblod.feature.user.model.UserDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LeaderboardRepositoryImpl (
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : LeaderboardRepository {

    private val usersCollection = db.collection("users")
    private val routesCollection = db.collection("routes")

    override fun observeUsers(): Flow<List<UserDto>> = callbackFlow {
        val sub = usersCollection.addSnapshotListener { snap, err ->
            if (err != null || snap == null) {
                close(err)
                return@addSnapshotListener
            }

            val list = snap.documents.mapNotNull { doc ->
                doc.toObject(UserDto::class.java)
                    ?.also { u -> u.uid = doc.id }
            }

            trySend(list)
        }

        awaitClose { sub.remove() }
    }

    override fun observeAllRoutes(): Flow<List<Route>> = callbackFlow {
        val sub = routesCollection.addSnapshotListener { snap, err ->
            if (err != null || snap == null) { close(err); return@addSnapshotListener }
            val list = snap.documents.mapNotNull { doc ->
                doc.toObject(Route::class.java)?.copy(id = doc.id)
            }
            trySend(list)
        }
        awaitClose { sub.remove() }
    }
}