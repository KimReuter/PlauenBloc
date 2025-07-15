package com.example.plauenblod.feature.communityPost.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plauenblod.feature.auth.viewmodel.AuthViewModel
import com.example.plauenblod.feature.communityPost.model.CommunityPost
import com.example.plauenblod.feature.communityPost.model.PostComment
import com.example.plauenblod.feature.communityPost.repository.PinBoardRepository
import com.example.plauenblod.feature.communityPost.repository.PinBoardRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PinboardViewModel(
    private val repository: PinBoardRepository,
    private val authViewModel: AuthViewModel
) : ViewModel() {
val currentUserId get() = authViewModel.userId.value

    private val _posts = MutableStateFlow<List<CommunityPost>>(emptyList())
    val posts: StateFlow<List<CommunityPost>> = _posts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.getAllPosts()
                    .collect { posts: List<CommunityPost>  ->
                        _posts.value = posts.sortedByDescending { it.timestamp.toInstant() }
                    }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createPost(post: CommunityPost) {
        viewModelScope.launch {
            try {
                repository.createPost(post)
                loadPosts()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun canEditPost(post: CommunityPost): Boolean {
        return post.authorId == currentUserId
    }

    fun updatePost(postId: String, newContent: String) {
        viewModelScope.launch {
            try {
                repository.updatePost(postId, newContent)
                loadPosts()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun updatePostIfAuthorized(post: CommunityPost, newContent: String) {
        if (!canEditPost(post)) return
        updatePost(post.id, newContent)
    }


    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                repository.deletePost(postId)
                loadPosts()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deletePostIfAuthorized(post: CommunityPost) {
        if (!canEditPost(post)) return
        deletePost(post.id)
    }

    fun addCommentToPost(postId: String, comment: PostComment) {
        viewModelScope.launch {
            try {
                repository.addComment(postId, comment)
                loadPosts()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun canEditComment(comment: PostComment): Boolean {
        return comment.authorId == currentUserId
    }

    fun updateCommentIfAuthorized(postId: String, comment: PostComment, newContent: String) {
        if (!canEditComment(comment)) return
        updateComment(postId, comment.id, newContent)
    }

    fun updateComment(postId: String, commentId: String, newContent: String) {
        viewModelScope.launch {
            try {
                repository.updateComment(postId, commentId, newContent)
                loadPosts()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteComment(postId: String, commentId: String) {
        viewModelScope.launch {
            try {
                repository.deleteComment(postId, commentId)
                loadPosts()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteCommentIfAuthorized(postId: String, comment: PostComment) {
        if (!canEditComment(comment)) return
        deleteComment(postId, comment.id)
    }
}