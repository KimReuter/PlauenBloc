package com.example.plauenblod.feature.routeCollection.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plauenblod.feature.routeCollection.model.RouteCollection
import com.example.plauenblod.feature.routeCollection.repository.RouteCollectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * ViewModel für das Management von Route-Sammlungen.
 */
class RouteCollectionViewModel : ViewModel(), KoinComponent {
    private val repository: RouteCollectionRepository by inject()

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
        description: String? = null,
        categories: List<String> = emptyList(),
        routeIds: List<String> = emptyList(),
        isPublic: Boolean = true
    ) = viewModelScope.launch {
        _error.value = null
        try {
            val now: Instant = Clock.System.now()
            val newCollection = RouteCollection(
                id = "",
                creatorId = creatorId,
                name = name,
                description = description,
                categories = categories,
                routeIds = routeIds,
                isPublic = isPublic,
                createdAt = now,
                updatedAt = now
            )
            repository.createCollection(newCollection)

            loadUserCollections(creatorId)
        } catch (e: Exception) {
            _error.value = e.localizedMessage
        }
    }

    fun updateCollection(collection: RouteCollection) = viewModelScope.launch {
        _error.value = null
        try {
            val updated = collection.copy(
                updatedAt = Clock.System.now()
            )
            repository.updateCollection(updated)

            loadCollection(updated.id)
        } catch (e: Exception) {
            _error.value = e.localizedMessage
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
