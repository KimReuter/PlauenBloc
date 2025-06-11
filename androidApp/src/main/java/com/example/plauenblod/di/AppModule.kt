package com.example.plauenblod.di

import com.example.plauenblod.viewmodel.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    includes(commonModule)

    single<CoroutineScope> {
        CoroutineScope(Dispatchers.Main)
    }

    single { AuthViewModel(get(), get()) }
}