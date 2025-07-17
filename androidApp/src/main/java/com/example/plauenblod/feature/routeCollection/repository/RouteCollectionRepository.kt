package com.example.plauenblod.feature.routeCollection.repository

import com.example.plauenblod.feature.routeCollection.model.RouteCollection
import kotlinx.coroutines.flow.Flow

interface RouteCollectionRepository {
    fun getAllPublicCollections(): Flow<List<RouteCollection>>
    fun getUserCollections(userId: String): Flow<List<RouteCollection>>
    fun getCollectionById(collectionId: String): Flow<RouteCollection?>

    suspend fun createCollection(collection: RouteCollection)
    suspend fun updateCollection(collection: RouteCollection)
    suspend fun deleteCollection(collectionId: String)
}