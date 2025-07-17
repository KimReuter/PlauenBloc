package com.example.plauenblod.feature.routeCollection.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plauenblod.android.util.FirestoreInstant
import com.example.plauenblod.feature.routeCollection.model.RouteCollection
import com.example.plauenblod.feature.routeCollection.repository.RouteCollectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock


class RouteCollectionViewModel(
    private val repository: RouteCollectionRepository
) : ViewModel(){

    private val _publicCollections = MutableStateFlow<List<RouteCollection>>(emptyList())
    val publicCollections: StateFlow<List<RouteCollection>> = _publicCollections

    private val _userCollections = MutableStateFlow<List<RouteCollection>>(emptyList())
    val userCollections: StateFlow<List<RouteCollection>> = _userCollections

    private val _selectedCollection = MutableStateFlow<RouteCollection?>(null)
    val selectedCollection: StateFlow<RouteCollection?> = _selectedCollection

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private fun refreshLists(userId: String?) {
        loadPublicCollections()
        userId?.let { loadUserCollections(it) }
    }

    fun loadPublicCollections() = viewModelScope.launch {
        _isLoading.value = true
        _error.value = null
        try {
            val list = repository.getAllPublicCollections()
            list.collect { collections ->
                _publicCollections.value = collections
            }
        } catch (e: Exception) {
            _error.value = e.localizedMessage
        } finally {
            _isLoading.value = false
        }
    }

    fun loadUserCollections(userId: String) = viewModelScope.launch {
        _isLoading.value = true
        _error.value = null
        try {
            val list = repository.getUserCollections(userId)
            list.collect { collections ->
                _userCollections.value = collections
            }
        } catch (e: Exception) {
            _error.value = e.localizedMessage
        } finally {
            _isLoading.value = false
        }
    }

    fun loadCollection(collectionId: String) {
        viewModelScope.launch {
            repository.getCollectionById(collectionId)
                .collect { collection ->
                    _selectedCollection.value = collection
                }
        }
    }

    fun createCollection(
        creatorId: String,
        name: String,
        description: String?,
        routeIds: List<String>,
        isPublic: Boolean
    ) = viewModelScope.launch {
        _isLoading.value = true
        _error.value = null
        try {
            val now = Clock.System.now()
            val newCol = RouteCollection(
                creatorId   = creatorId,
                name        = name,
                description = description,
                isPublic    = isPublic,
                routeIds    = routeIds,
                createdAt   = FirestoreInstant.fromInstant(now),
                updatedAt   = FirestoreInstant.fromInstant(now)
            )
            Log.d("RCViewModel", "⏳ Creating: $newCol")
            val newId = repository.createCollection(newCol)
            Log.d("RCViewModel", "✅ Created collection with id=$newId")
            loadCollection(newId)
            refreshLists(creatorId)
        } catch (e: Exception) {
            Log.e("RCViewModel", "❌ Fehler beim Erstellen:", e)
            _error.value = e.localizedMessage
        } finally {
            _isLoading.value = false
        }
    }

    fun updateCollection(collection: RouteCollection) = viewModelScope.launch {
        _isLoading.value = true
        _error.value = null
        try {
            val updated = collection.copy(
                updatedAt = FirestoreInstant.fromInstant(Clock.System.now())
            )
            repository.updateCollection(updated)

            _selectedCollection.value = updated

            refreshLists(updated.creatorId)

        } catch (e: Exception) {
            _error.value = e.message
        } finally {
            _isLoading.value = false
        }
    }

    fun deleteCollection(collectionId: String, userId: String) = viewModelScope.launch {
        _error.value = null
        try {
            repository.deleteCollection(collectionId)

            loadUserCollections(userId)
        } catch (e: Exception) {
            _error.value = e.localizedMessage
        }
    }
}
