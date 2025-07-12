package com.example.plauenblod.feature.route.viewmodel

import com.example.plauenblod.feature.auth.UserRole
import com.example.plauenblod.feature.route.model.Route
import com.example.plauenblod.feature.route.model.RouteFilter
import com.example.plauenblod.feature.route.model.util.calculatePoints
import com.example.plauenblod.feature.route.repository.RouteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RouteViewModel(
    private val routeRepository: RouteRepository,
    private val coroutineScope: CoroutineScope
) {
    private val _routeCreated = MutableStateFlow<Result<Unit>?>(null)
    val routeCreated: StateFlow<Result<Unit>?> = _routeCreated

    private val _routeEdited = MutableStateFlow<Result<Unit>?>(null)
    val routeEdited: StateFlow<Result<Unit>?> = _routeEdited

    private val _allRoutes = MutableStateFlow<List<Route>>(emptyList())
    val allRoutes: StateFlow<List<Route>> = _allRoutes

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val searchedRoutes: StateFlow<List<Route>> = combine(
        allRoutes, searchQuery
    ) { routes, query ->
        if (query.isBlank()) {
            routes
        } else {
            routes.filter { it.name.contains(query, ignoreCase = true) }
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _availableSetters = MutableStateFlow(listOf("Jens Grimm", "Jörg Schwerdt", "Jörg Band"))
    val availableSetters = _availableSetters.asStateFlow()

    private val _filterState = MutableStateFlow(RouteFilter())
    val filterState = _filterState.asStateFlow()

    val filteredRoutes = combine(_allRoutes, _filterState) { routes, filter ->
        routes.filter { route ->
            (filter.holdColor == null || route.holdColor == filter.holdColor) &&
                    (filter.difficulty == null || route.difficulty == filter.difficulty) &&
                    (filter.sector == null || route.sector == filter.sector) &&
                    (filter.hall == null || route.hall == filter.hall) &&
                    (filter.routeSetter.isNullOrBlank() || route.setter.equals(filter.routeSetter, true))
        }
    }

    fun createRoute(route: Route, onResult: (Boolean) -> Unit = {}) {
        coroutineScope.launch {
            try {
                if (route.name.isBlank() || route.description.isBlank() || route.setter.isBlank()) {
                    _errorMessage.value = "Bitte fülle alle Pflichtfelder aus."
                    onResult(false)
                    return@launch
                }

                val points = calculatePoints(route.difficulty, isFlash = false)
                val routeWithPoints = route.copy(points = points)

                val result = routeRepository.createRoute(routeWithPoints)
                if (result.isFailure) {
                    val error = result.exceptionOrNull()?.message ?: "Unbekannter Fehler"
                    _errorMessage.value = error
                    println("❌ RouteViewModel → createRoute(): Fehler: $error")
                    onResult(false)
                } else {
                    _errorMessage.value = null
                    println("✅ RouteViewModel → createRoute(): Erfolgreich!")
                    loadAllRoutes()
                    onResult(true)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unbekannter Fehler"
                println("❌ RouteViewModel → createRoute(): Exception: ${e.message}")
                onResult(false)
            }
        }
    }

    fun addSetterIfNew(setter: String) {
        if (setter.isNotBlank() && setter !in _availableSetters.value) {
            _availableSetters.value = _availableSetters.value + setter
        }
    }

    fun applyFilter(filter: RouteFilter) {
        filter.routeSetter?.let { addSetterIfNew(it) }
        _filterState.value = filter
    }

    fun clearRouteCreatedStatus() {
        _routeCreated.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun loadAllRoutes() {
        coroutineScope.launch {
            val allRoutes = routeRepository.getAllRoutes()
            _allRoutes.value = allRoutes
        }
    }

    fun searchRoutesByName(query: String) {
        coroutineScope.launch {
            val allRoutes = routeRepository.getAllRoutes()
            val filtered = allRoutes.filter { it.name.contains(query, ignoreCase = true) }
            _allRoutes.value = filtered
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun deleteRoute(userRole: UserRole, id: String, onResult: (Boolean) -> Unit = {}) {
        coroutineScope.launch {
            try {
                if (userRole != UserRole.OPERATOR) {
                    _errorMessage.value = "Nur Betreiber dürfen Routen löschen."
                    onResult(false)
                    return@launch
                }
                routeRepository.deleteRoute(id)
                loadAllRoutes()
                _errorMessage.value = null
                onResult(true)
            } catch (e: Exception) {
                _errorMessage.value = "Fehler beim Löschen der Route: ${e.message}"
                onResult(false)
            }
        }
    }

    fun editRoute(userRole: UserRole, routeId: String, route: Route, onResult: (Boolean) -> Unit = {}) {
        coroutineScope.launch {
            try {
                if (userRole != UserRole.OPERATOR) {
                    _errorMessage.value = "Nur Betreiber dürfen Routen ändern."
                    onResult(false)
                    return@launch
                }

                val result = routeRepository.editRoute(routeId, route)
                if (result.isSuccess) {
                    println("✅ RouteViewModel → editRoute(): Erfolgreich bearbeitet!")
                    _errorMessage.value = null
                    loadAllRoutes()
                    onResult(true)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Bearbeiten fehlgeschlagen."
                    _errorMessage.value = error
                    println("❌ RouteViewModel → editRoute(): Fehler: $error")
                    onResult(false)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unbekannter Fehler beim Bearbeiten."
                println("❌ RouteViewModel → editRoute(): Exception: ${e.message}")
                onResult(false)
            }
        }
    }

    init {
        loadAllRoutes()
    }
}