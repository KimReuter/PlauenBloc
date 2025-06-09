package com.example.plauenblod.di

import com.example.plauenblod.data.auth.AuthRepository
import com.example.plauenblod.data.auth.FirebaseAuthRepository
import com.example.plauenblod.viewmodel.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val appModule = module {

    single<AuthRepository> {
        FirebaseAuthRepository()
    }

    single<CoroutineScope> {
        CoroutineScope(Dispatchers.Main)
    }

    single { AuthViewModel(get(), get()) }

}