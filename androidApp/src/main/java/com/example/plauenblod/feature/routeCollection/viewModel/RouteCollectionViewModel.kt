package com.example.plauenblod.feature.routeCollection.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plauenblod.android.util.FirestoreInstant
import com.example.plauenblod.android.util.toFirestoreInstant
import com.example.plauenblod.feature.routeCollection.model.RouteCollection
import com.example.plauenblod.feature.routeCollection.repository.RouteCollectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock


class RouteCollectionViewModel(
    private val repository: RouteCollectionRepository
) : ViewModel() {

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

    fun loadPublicCollections() {
        _isLoading.value = true
        _error.value = null

        repository.getAllPublicCollections()
            .onEach { collections ->
                Log.d("RouteCollectionVM", "publicCollections arrived: ${collections.size} items")
                _publicCollections.value = collections
                if (_isLoading.value) _isLoading.value = false
            }
            .catch { e ->
                Log.e("RouteCollectionVM", "loadPublicCollections failed", e)
                _error.value = e.localizedMessage
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }

    fun loadUserCollections(userId: String) {
        _isLoading.value = true
        _error.value = null

        repository.getUserCollections(userId)
            .onEach { collections ->
                Log.d("RouteCollectionVM", "userCollections arrived: ${collections.size}")
                _userCollections.value = collections
                if (_isLoading.value) _isLoading.value = false
            }
            .catch { e ->
                Log.e("RouteCollectionVM", "loadUserCollections failed", e)
                _error.value = e.localizedMessage
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
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
                creatorId = creatorId,
                name = name,
                description = description,
                `public` = isPublic,
                routeIds = routeIds,
                createdAt = FirestoreInstant.fromInstant(now),
                updatedAt = FirestoreInstant.fromInstant(now)
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

    fun toggleLike(collectionId: String, userId: String) {
        viewModelScope.launch {
            repository.toggleLike(collectionId, userId)
        }
    }

    fun toggleDone(routeId: String, done: Boolean) = viewModelScope.launch {
        _selectedCollection.value?.let { coll ->
            val newDone = if (done)
                (coll.doneRouteIds + routeId).distinct()
            else
                coll.doneRouteIds - routeId

            val updated = coll.copy(
                doneRouteIds = newDone,
                updatedAt = Clock.System.now().toFirestoreInstant()
            )

            repository.updateCollection(updated)
            _selectedCollection.value = updated
            refreshLists(updated.creatorId)
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
