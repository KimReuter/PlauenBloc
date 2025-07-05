package com.example.plauenblod.feature.route.viewmodel

import com.example.plauenblod.feature.auth.UserRole
import com.example.plauenblod.feature.route.model.Route
import com.example.plauenblod.feature.route.model.util.calculatePoints
import com.example.plauenblod.feature.route.repository.RouteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RouteViewModel(
    private val routeRepository: RouteRepository,
    private val coroutineScope: CoroutineScope
) {
    private val _routeCreated = MutableStateFlow<Result<Unit>?>(null)
    val routeCreated: StateFlow<Result<Unit>?> = _routeCreated

    private val _routeEdited = MutableStateFlow<Result<Unit>?>(null)
    val routeEdited: StateFlow<Result<Unit>?> = _routeEdited

    private val _routes = MutableStateFlow<List<Route>>(emptyList())
    val routes: StateFlow<List<Route>> = _routes

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

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
                    loadRoutes()
                    onResult(true)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unbekannter Fehler"
                println("❌ RouteViewModel → createRoute(): Exception: ${e.message}")
                onResult(false)
            }
        }
    }

    fun clearRouteCreatedStatus() {
        _routeCreated.value = null
    }

    fun clearRouteEditedStatus() {
        _routeEdited.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun loadRoutes() {
        coroutineScope.launch {
            _routes.value = routeRepository.getAllRoutes()
        }
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
                loadRoutes()
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
                    loadRoutes()
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
        loadRoutes()
    }
}