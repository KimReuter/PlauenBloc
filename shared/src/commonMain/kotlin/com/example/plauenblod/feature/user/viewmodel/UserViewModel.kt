package com.example.plauenblod.feature.user.viewmodel

import com.example.plauenblod.feature.route.model.Route
import com.example.plauenblod.feature.user.model.UserDto
import com.example.plauenblod.feature.user.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository,
    private val coroutineScope: CoroutineScope
) {
    private val _userState = MutableStateFlow<UserDto?>(null)
    val userState: StateFlow<UserDto?> = _userState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName

    private val _userProfileImageUrl = MutableStateFlow<String?>(null)
    val userProfileImageUrl: StateFlow<String?> = _userProfileImageUrl

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _allUsers = MutableStateFlow<List<UserDto>>(emptyList())
    val allUsers: StateFlow<List<UserDto>> = _allUsers


    val filteredUsers: StateFlow<List<UserDto>> = combine(
        allUsers,
        searchQuery
    ) { users, query ->
        if (query.isBlank()) users
        else users.filter { it.userName?.contains(query, ignoreCase = true) == true }
    }.stateIn(
        coroutineScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun loadUser(userId: String, onResult: (Boolean) -> Unit = {}) {
        coroutineScope.launch {
            _isLoading.value = true
            try {
                val result = userRepository.getUserById(userId)
                result.fold(
                    onSuccess = { user ->
                        println("✅ User erfolgreich geladen: $user")
                        _userState.value = user
                        _errorMessage.value = null
                        onResult(true)
                    },
                    onFailure = { throwable ->
                        println("❌ Fehler beim fold: ${throwable.message}")
                        _userState.value = null
                        _errorMessage.value = throwable.message ?: "Unbekannter Fehler"
                        onResult(false)
                    }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun loadAllUsers() {
        coroutineScope.launch {
            userRepository.getAllUsers()
                .collect { users ->
                    _allUsers.value = users
                }
        }
    }

    fun tickRoute(
        userId: String,
        route: Route,
        attempts: Int,
        isFlash: Boolean,
        onResult: (Boolean) -> Unit
    ) {
        println("✅ tickRoute wird ausgeführt")
        coroutineScope.launch {
            val result = userRepository.tickRoute(userId, route, attempts, isFlash)
            result.fold(
                onSuccess = {
                    println("✅ Tick erfolgreich")
                    onResult(true)
                },
                onFailure = { throwable ->
                    println("❌ Fehler im fold: ${throwable.stackTraceToString()}")
                    println("❌ tickRoute Exception: ${throwable.message}")
                    onResult(false)
                }
            )
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun resetUser() {
        _userState.value = null
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}