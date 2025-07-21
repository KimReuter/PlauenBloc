package com.example.plauenblod.feature.dashboard.viewModel

import com.example.plauenblod.feature.auth.UserRole
import com.example.plauenblod.feature.auth.viewmodel.AuthViewModel
import com.example.plauenblod.feature.dashboard.model.NewsPost
import com.example.plauenblod.feature.dashboard.repository.NewsPostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    private val newsRepo: NewsPostRepository,
    private val authViewModel: AuthViewModel,
    private val coroutineScope: CoroutineScope
) {
    private val _news = MutableStateFlow<List<NewsPost>>(emptyList())
    val news: StateFlow<List<NewsPost>> = _news.asStateFlow()

    private val _showEditDialog = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog.asStateFlow()

    private val _editingPost = MutableStateFlow<NewsPost?>(null)
    val editingPost: StateFlow<NewsPost?> = _editingPost.asStateFlow()

    val currentUserId: StateFlow<String?> = authViewModel.userId
    val userRole: StateFlow<UserRole?>      = authViewModel.userRole

    private val _selectedPost = MutableStateFlow<NewsPost?>(null)
    val selectedPost: StateFlow<NewsPost?> = _selectedPost.asStateFlow()

    val isOperator: StateFlow<Boolean> = userRole
        .map { it == UserRole.OPERATOR }
        .stateIn(coroutineScope, SharingStarted.Eagerly, false)

    init {
        coroutineScope.launch {
            newsRepo.observeNews().collect { _news.value = it }
        }
    }

    fun postNews(title: String, content: String, imageUrl: String?) {
        val author = currentUserId.value ?: return
        coroutineScope.launch {
            val post = NewsPost(
                title      = title,
                content    = content,
                imageUrl   = imageUrl,
                authorId   = author,
                timestamp  = System.currentTimeMillis()
            )
            newsRepo.createPost(post)
        }
    }

    fun startEditingPost(post: NewsPost) {
        _editingPost.value = post
        _showEditDialog.value = true
    }

    fun deletePost(postId: String) {
        coroutineScope.launch {
            newsRepo.deletePost(postId)
        }
    }

    fun updatePost(post: NewsPost) {
        coroutineScope.launch {
            newsRepo.updatePost(post)
        }
    }

    suspend fun loadPost(postId: String) {
        val post = newsRepo.getPostById(postId)
        _selectedPost.value = post
    }

    fun clearSelectedPost() {
        _selectedPost.value = null
    }
}