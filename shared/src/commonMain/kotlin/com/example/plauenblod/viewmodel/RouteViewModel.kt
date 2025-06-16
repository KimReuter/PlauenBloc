package com.example.plauenblod.viewmodel

import com.example.plauenblod.data.route.RouteRepository
import com.example.plauenblod.model.HallSection
import com.example.plauenblod.model.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class NavigationEvent {
    data class NavigateToSelectPoint(val hallSection: HallSection) : NavigationEvent()
}

class RouteViewModel (
    private val repo: RouteRepository,
    private val scope: CoroutineScope
) {
    private val _routeCreated = MutableStateFlow<Result<Unit>?>(null)
    val routeCreated: StateFlow<Result<Unit>?> = _routeCreated

    private val _navigation = MutableSharedFlow<NavigationEvent>()
    val navigation = _navigation.asSharedFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun createRoute(route: Route) {
        scope.launch {
            if (route.name.isBlank() || route.description.isBlank() || route.setter.isBlank()) {
                _errorMessage.value = "Bitte fülle alle Pflichtfelder aus."
                return@launch
            }

            val result = repo.createRoute(route)
            _routeCreated.value = result
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Ein Fehler ist aufgetreten."
            } else {
                _errorMessage.value = null
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun onSelectPointClicked(hallSection: HallSection) {
        scope.launch {
            _navigation.emit(NavigationEvent.NavigateToSelectPoint(hallSection))
        }
    }

}