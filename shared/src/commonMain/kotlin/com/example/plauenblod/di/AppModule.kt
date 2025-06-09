package com.example.plauenblod.di

import com.example.plauenblod.data.auth.AuthRepository
import com.example.plauenblod.data.auth.FirebaseAuthRepository
import org.koin.dsl.module

val appModule = module {

    single<AuthRepository> {
        FirebaseAuthRepository()
    }

}