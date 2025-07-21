package com.example.plauenblod.feature.communityPost.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plauenblod.feature.auth.viewmodel.AuthViewModel
import com.example.plauenblod.feature.communityPost.model.CommunityPost
import com.example.plauenblod.feature.communityPost.model.PostComment
import com.example.plauenblod.feature.communityPost.repository.PinBoardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
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

    private val userReactions = mutableMapOf<String, String>()

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
            Log.d("PinboardVM", "🔥 createPost wird ausgeführt: $post")
            try {
                repository.createPost(post)
                loadPosts()
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("PinboardVM", "Fehler beim createPost", e)
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

    fun commentsFor(postId: String) = repository.observeComments(postId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
        Log.d("PinboardVM", "🗑️ Deleting comment ${comment.id} of post $postId")
        deleteComment(postId, comment.id)
    }

    fun toggleReaction(postId: String, emoji: String) {
        viewModelScope.launch {
            try {
                val previous = userReactions[postId]
                if (previous == emoji) {
                    repository.removeReaction(postId, emoji)
                    userReactions.remove(postId)
                } else {
                    if (previous != null) {
                        repository.removeReaction(postId, previous)
                    }
                    repository.addReaction(postId, emoji)
                    userReactions[postId] = emoji
                }
                loadPosts()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}