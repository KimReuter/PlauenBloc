package com.example.plauenblod.data.model

import com.squareup.moshi.Json

data class CloudinaryUploadResponse(
    @Json(name = "secure_url") val secureUrl: String,
    @Json(name = "public_id") val publicId: String
)