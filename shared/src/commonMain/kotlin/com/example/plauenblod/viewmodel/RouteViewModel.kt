package com.example.plauenblod.viewmodel

import com.example.plauenblod.data.route.RouteRepository
import com.example.plauenblod.model.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class RouteViewModel (
    private val repo: RouteRepository,
    private val scope: CoroutineScope
) {
    private val _routeCreated = MutableStateFlow<Result<Unit>?>(null)
    val routeCreated: StateFlow<Result<Unit>?> = _routeCreated

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
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun loadRoutes() {
        scope.launch {
            _routes.value = repo.getAllRoutes()
        }
    }

    init {
        loadRoutes()
    }
}