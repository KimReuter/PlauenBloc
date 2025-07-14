package com.example.plauenblod.feature.imageUpload.repository

import android.net.Uri

interface CloudinaryRepository {
    suspend fun uploadImage(uri: Uri): String
}