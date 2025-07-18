package com.example.plauenblod.feature.routeCollection.repository

import android.util.Log
import com.example.plauenblod.feature.routeCollection.model.RouteCollection
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RouteCollectionRepositoryImpl(
    private val firestore: FirebaseFirestore = Firebase.firestore
) : RouteCollectionRepository {
    private val collectionsRef = firestore.collection("route_collections")

    override fun getAllPublicCollections(): Flow<List<RouteCollection>> = callbackFlow {
        val listener = collectionsRef
            .whereEqualTo("public", true)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    Log.w("RCRepo", "Firestore publicCollections listener error", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot.documents.mapNotNull { doc ->
                    val coll = doc.toObject<RouteCollection>() ?: return@mapNotNull null
                    coll.copy(id = doc.id)
                }
                Log.d("RCRepo", "Firestore returned public collections: ${list.size}")
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override fun getUserCollections(userId: String): Flow<List<RouteCollection>> = callbackFlow {
        val listener = collectionsRef
            .whereEqualTo("creatorId", userId)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    Log.w("RCRepo", "Firestore userCollections listener error", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot.documents.mapNotNull { doc ->
                    val coll = doc.toObject<RouteCollection>() ?: return@mapNotNull null
                    coll.copy(id = doc.id)
                }
                Log.d("RCRepo", "Firestore returned user collections: ${list.size}")
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override fun getCollectionById(collectionId: String): Flow<RouteCollection?> = callbackFlow {
        val docRef = collectionsRef.document(collectionId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null || !snapshot.exists()) {
                trySend(null)
                return@addSnapshotListener
            }
            val collection = snapshot.toObject<RouteCollection>()
            trySend(collection?.copy(id = snapshot.id))
        }
        awaitClose { listener.remove() }
    }

    override suspend fun createCollection(collection: RouteCollection): String {
        try {
            Log.d("RCRepo", "⏳ createCollection: sende an Firestore: $collection")
            val docRef = collectionsRef
                .add(collection.copy(id = ""))
                .await()
            Log.d("RCRepo", "✅ createCollection: erfolgreich mit id=${docRef.id}")
            return docRef.id
        } catch (e: Exception) {
            Log.e("RCRepo", "❌ createCollection failed", e)
            throw e
        }
    }

    override suspend fun updateCollection(collection: RouteCollection) {
        collectionsRef
            .document(collection.id)
            .set(collection, SetOptions.merge())
            .await()
    }

    override suspend fun deleteCollection(collectionId: String) {
        collectionsRef
            .document(collectionId)
            .delete()
            .await()
    }
}