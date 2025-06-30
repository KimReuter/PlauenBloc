package com.example.plauenblod.viewmodel

import com.example.plauenblod.data.route.RouteRepository
import com.example.plauenblod.model.Route
import com.example.plauenblod.model.UserRole
import com.example.plauenblod.viewmodel.state.DialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class RouteViewModel(
    private val repo: RouteRepository,
    private val scope: CoroutineScope
) {
    private val _routeCreated = MutableStateFlow<Result<Unit>?>(null)
    val routeCreated: StateFlow<Result<Unit>?> = _routeCreated

    private val _routeEdited = MutableStateFlow<Result<Unit>?>(null)
    val routeEdited: StateFlow<Result<Unit>?> = _routeEdited

    private val _routes = MutableStateFlow<List<Route>>(emptyList())
    val routes: StateFlow<List<Route>> = _routes

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun createRoute(route: Route) {
        scope.launch {
            println("📌 RouteViewModel → createRoute(): Erhalte Route: $route")

            if (route.name.isBlank() || route.description.isBlank() || route.setter.isBlank()) {
                _errorMessage.value = "Bitte fülle alle Pflichtfelder aus."
                println("⚠️ RouteViewModel → createRoute(): Validierung fehlgeschlagen (Name/Beschreibung/Setter leer)")
                return@launch
            }
            val result = repo.createRoute(route)
            _routeCreated.value = result
            if (result.isFailure) {
                val error = result.exceptionOrNull()?.message ?: "Unbekannter Fehler"
                _errorMessage.value = error
                println("❌ RouteViewModel → createRoute(): Fehler: $error")
            } else {
                println("✅ RouteViewModel → createRoute(): Erfolgreich!")
                _errorMessage.value = null
                loadRoutes()
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
        scope.launch {
            _routes.value = repo.getAllRoutes()
        }
    }

    fun deleteRoute(userRole: UserRole, id: String) {
        scope.launch {
            if (userRole != UserRole.OPERATOR) {
                _errorMessage.value = "Nur Betreiber dürfen Routen löschen."
                return@launch
            }
            runCatching {
                repo.deleteRoute(id)
            }.onSuccess {
                loadRoutes()
            }.onFailure { e ->
                _errorMessage.value = "Fehler beim Löschen der Route: ${e.message}"
            }
        }
    }

    fun editRoute(userRole: UserRole, routeId: String, route: Route) {
        println("✏️ Bearbeiten gestartet für Route $routeId")

        scope.launch {
            if (userRole != UserRole.OPERATOR) {
                _errorMessage.value = "Nur Betreiber dürfen Routen ändern."
                return@launch
            }

            try {
                val result = repo.editRoute(routeId, route) // gibt Result<Unit> zurück
                _routeEdited.value = result

                if (result.isSuccess) {
                    println("✅ RouteViewModel → editRoute(): Erfolgreich bearbeitet!")
                    _errorMessage.value = null
                    loadRoutes()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Bearbeiten fehlgeschlagen."
                    _errorMessage.value = error
                    println("❌ RouteViewModel → editRoute(): Fehler: $error")
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unbekannter Fehler beim Bearbeiten."
                println("❌ RouteViewModel → editRoute(): Exception: ${e.message}")
            }
        }
    }

    init {
        loadRoutes()
    }
}