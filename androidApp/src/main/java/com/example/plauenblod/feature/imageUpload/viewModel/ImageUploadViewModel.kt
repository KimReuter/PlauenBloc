package com.example.plauenblod.feature.imageUpload.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plauenblod.feature.imageUpload.model.ImageUploadState
import com.example.plauenblod.feature.imageUpload.repository.CloudinaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ImageUploadViewModel(
    private val repository: CloudinaryRepository
) : ViewModel() {

    private val _uploadState = MutableStateFlow<ImageUploadState>(ImageUploadState.Idle)
    val uploadState: StateFlow<ImageUploadState> = _uploadState

    fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            _uploadState.value = ImageUploadState.Loading
            try {
                val imageUrl = repository.uploadImage(uri)
                _uploadState.value = ImageUploadState.Success(imageUrl)
            } catch (e: Exception) {
                _uploadState.value = ImageUploadState.Error(e.localizedMessage ?: "Unbekannter Fehler")
            }
        }
    }

    fun resetState() {
        _uploadState.value = ImageUploadState.Idle
    }
}