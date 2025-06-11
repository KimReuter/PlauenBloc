package com.example.plauenblod.di

import com.example.plauenblod.data.auth.AuthRepository
import com.example.plauenblod.data.auth.FirebaseAuthRepository
import org.koin.dsl.module

val commonModule = module {
    single<AuthRepository> { FirebaseAuthRepository() }
}