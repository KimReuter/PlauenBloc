package com.example.plauenblod.di

import com.example.plauenblod.feature.auth.repository.AuthRepository
import com.example.plauenblod.feature.auth.repository.FirebaseAuthRepository
import com.example.plauenblod.feature.route.repository.FireBaseRouteRepository
import com.example.plauenblod.feature.route.repository.RouteRepository
import com.example.plauenblod.feature.routeReview.repository.FireBaseRouteReviewRepository
import com.example.plauenblod.feature.routeReview.repository.RouteReviewRepository
import com.example.plauenblod.feature.auth.viewmodel.AuthViewModel
import com.example.plauenblod.feature.chat.repository.ChatRepository
import com.example.plauenblod.feature.chat.repository.FirebaseChatRepository
import com.example.plauenblod.feature.chat.viewmodel.ChatViewModel
import com.example.plauenblod.feature.routeReview.viewmodel.RouteReviewViewModel
import com.example.plauenblod.feature.route.viewmodel.RouteViewModel
import com.example.plauenblod.feature.user.repository.FirebaseUserRepository
import com.example.plauenblod.feature.user.repository.UserRepository
import com.example.plauenblod.feature.user.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val appModule = module {

    includes(commonModule)

    single<CoroutineScope> { CoroutineScope(Dispatchers.Main) }

    single<AuthRepository> { FirebaseAuthRepository() }

    single<RouteRepository> { FireBaseRouteRepository() }

    single<RouteReviewRepository> { FireBaseRouteReviewRepository() }

    single<UserRepository> { FirebaseUserRepository() }

    single<ChatRepository> { FirebaseChatRepository() }

    single { AuthViewModel(get(), get()) }

    single { RouteViewModel(get(), get()) }

    single { RouteReviewViewModel(get(), get()) }

    single { UserViewModel(get(), get()) }

    single { ChatViewModel(get(), get(), get()) }

}