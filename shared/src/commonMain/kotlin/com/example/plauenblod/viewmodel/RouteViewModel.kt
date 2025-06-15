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

    fun createRoute(route: Route) {
        scope.launch {
            val result = repo.createRoute(route)
            _routeCreated.value = result
        }
    }
}