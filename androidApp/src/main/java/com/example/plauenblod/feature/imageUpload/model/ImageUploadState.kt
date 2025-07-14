package com.example.plauenblod.feature.imageUpload.model

sealed class ImageUploadState {
    object Idle : ImageUploadState()
    object Loading : ImageUploadState()
    data class Success(val imageUrl: String) : ImageUploadState()
    data class Error(val message: String) : ImageUploadState()
}