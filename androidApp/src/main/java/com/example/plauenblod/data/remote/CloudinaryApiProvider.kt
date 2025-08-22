package com.example.plauenblod.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

fun createCloudinaryApi(): CloudinaryApi {
    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.cloudinary.com/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    return retrofit.create(CloudinaryApi::class.java)
}