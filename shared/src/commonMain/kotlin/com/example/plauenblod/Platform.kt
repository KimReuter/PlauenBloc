package com.example.plauenblod

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform