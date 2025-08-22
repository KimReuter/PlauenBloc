package com.example.plauenblod.feature.routeCollection.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plauenblod.feature.route.model.Route
import com.example.plauenblod.feature.route.repository.RouteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RouteSelectionViewModel(
    private val routeRepo: RouteRepository
) : ViewModel() {

    private val _allRoutes = MutableStateFlow<List<Route>>(emptyList())
    val allRoutes: StateFlow<List<Route>> = _allRoutes

    private val _selectedRouteIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedRouteIds: StateFlow<Set<String>> = _selectedRouteIds

    init {
        viewModelScope.launch {
            val routes = routeRepo.getAllRoutes()
            _allRoutes.value = routes
        }
    }

    fun toggleSelection(routeId: String) {
        val current = _selectedRouteIds.value.toMutableSet()
        if (!current.add(routeId)) current.remove(routeId)
        _selectedRouteIds.value = current
    }

    fun setSelection(ids: List<String>) {
        _selectedRouteIds.value = ids.toSet()
    }

    fun clearSelection() {
        _selectedRouteIds.value = emptySet()
    }
}