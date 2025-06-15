package com.example.plauenblod.di

import com.example.plauenblod.data.auth.AuthRepository
import com.example.plauenblod.data.auth.FirebaseAuthRepository
import com.example.plauenblod.data.route.FireBaseRouteRepository
import com.example.plauenblod.data.route.RouteRepository
import com.example.plauenblod.viewmodel.AuthViewModel
import com.example.plauenblod.viewmodel.RouteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    includes(commonModule)

    single<CoroutineScope> { CoroutineScope(Dispatchers.Main) }

    single { AuthViewModel(get(), get()) }

    single<RouteRepository> { FireBaseRouteRepository() }

    single<AuthRepository> { FirebaseAuthRepository() }

    single { RouteViewModel(get(), get()) }
}