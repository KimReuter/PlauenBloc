package com.example.plauenblod.di

import com.example.plauenblod.data.remote.createCloudinaryApi
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
import com.example.plauenblod.feature.communityPost.repository.PinBoardRepository
import com.example.plauenblod.feature.communityPost.repository.PinBoardRepositoryImpl
import com.example.plauenblod.feature.communityPost.viewModel.PinboardViewModel
import com.example.plauenblod.feature.dashboard.repository.NewsPostRepository
import com.example.plauenblod.feature.dashboard.repository.NewsPostRepositoryImpl
import com.example.plauenblod.feature.dashboard.viewModel.DashboardViewModel
import com.example.plauenblod.feature.imageUpload.repository.CloudinaryRepository
import com.example.plauenblod.feature.imageUpload.repository.CloudinaryRepositoryImpl
import com.example.plauenblod.feature.imageUpload.viewModel.ImageUploadViewModel
import com.example.plauenblod.feature.ranking.repository.LeaderboardRepository
import com.example.plauenblod.feature.ranking.repository.LeaderboardRepositoryImpl
import com.example.plauenblod.feature.ranking.viewModel.LeaderboardViewModel
import com.example.plauenblod.feature.routeReview.viewmodel.RouteReviewViewModel
import com.example.plauenblod.feature.route.viewmodel.RouteViewModel
import com.example.plauenblod.feature.routeCollection.repository.RouteCollectionRepository
import com.example.plauenblod.feature.routeCollection.repository.RouteCollectionRepositoryImpl
import com.example.plauenblod.feature.routeCollection.viewModel.RouteCollectionViewModel
import com.example.plauenblod.feature.routeCollection.viewModel.RouteSelectionViewModel
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

    single<CloudinaryRepository> { CloudinaryRepositoryImpl(get(), get()) }

    single<PinBoardRepository> { PinBoardRepositoryImpl() }

    single<RouteCollectionRepository> { RouteCollectionRepositoryImpl() }

    single<NewsPostRepository> { NewsPostRepositoryImpl() }

    single<LeaderboardRepository> { LeaderboardRepositoryImpl() }

    single { AuthViewModel(get(), get()) }

    single { RouteViewModel(get(), get()) }

    single { RouteReviewViewModel(get(), get(), get(), get()) }

    single { UserViewModel(get(), get(), get()) }

    single { ChatViewModel(get(), get(), get(), get(), get()) }

    single { ImageUploadViewModel(get()) }

    single { PinboardViewModel(get(), get()) }

    single { RouteCollectionViewModel(get()) }

    single { RouteSelectionViewModel(get()) }

    single { DashboardViewModel(get(), get(), get()) }

    single { LeaderboardViewModel(get(), get()) }

    single { createCloudinaryApi() }

}