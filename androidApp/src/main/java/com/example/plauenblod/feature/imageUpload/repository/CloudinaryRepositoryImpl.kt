package com.example.plauenblod.feature.imageUpload.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.plauenblod.data.remote.CloudinaryApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CloudinaryRepositoryImpl(
    private val api: CloudinaryApi,
    private val context: Context
) : CloudinaryRepository {

    override suspend fun uploadImage(uri: Uri): String {
        Log.d("CloudinaryUpload", "📡 Starte Upload mit URI: $uri")

        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        inputStream?.use { input ->
            tempFile.outputStream().use { output -> input.copyTo(output) }
        }

        val requestFile = tempFile
            .asRequestBody("image/*".toMediaTypeOrNull())

        val multipart = MultipartBody.Part.createFormData(
            "file", tempFile.name, requestFile
        )

        val uploadPreset = "plauenbloc_upload".toRequestBody("text/plain".toMediaType())


        val response = api.uploadImage(
            file = multipart,
            uploadPreset = uploadPreset
        )

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Log.d("CloudinaryUpload", "✅ Upload erfolgreich. URL: ${body.secureUrl}")
                return body.secureUrl
            } else {
                Log.e("CloudinaryUpload", "❌ Upload fehlgeschlagen: Leerer Body bei erfolgreichem Response.")
                throw Exception("Leere Antwort von Cloudinary")
            }
        } else {
            val errorBody = response.errorBody()?.string()
            Log.e(
                "CloudinaryUpload",
                "❌ Upload fehlgeschlagen: HTTP ${response.code()}\nFehlermeldung: $errorBody"
            )
            throw Exception("Upload fehlgeschlagen mit Status ${response.code()}")
        }
    }
}