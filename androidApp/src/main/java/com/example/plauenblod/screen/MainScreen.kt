package com.example.plauenblod.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.plauenblod.feature.navigation.TabItem
import com.example.plauenblod.feature.auth.viewmodel.AuthViewModel
import com.example.plauenblod.feature.authentication.screen.AuthScreen
import com.example.plauenblod.feature.chat.screen.ChatScreen
import com.example.plauenblod.feature.chat.viewmodel.ChatViewModel
import com.example.plauenblod.feature.community.screen.CommunityScreen
import com.example.plauenblod.feature.communityPost.viewModel.PinboardViewModel
import com.example.plauenblod.feature.route.screen.RouteDetailScreen
import com.example.plauenblod.feature.route.screen.RouteScreen
import com.example.plauenblod.feature.user.screen.OwnProfileScreen
import com.example.plauenblod.feature.user.screen.UserProfileScreen
import com.example.plauenblod.feature.user.viewmodel.UserViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
object AuthRoute

@Serializable
object DashboardRoute

@Serializable
object BoulderRoute

@Serializable
object CollectionRoute

@Serializable
object CommunityRoute

@Serializable
object OwnProfileRoute

@Serializable
data class UserProfileRoute(
    val userId: String
)

@Serializable
data class BoulderDetailRoute(
    val routeId: String
)

@Serializable
data class ChatRoute(
    val currentUserId: String,
    val targetUserId: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppStart(
    selectedTab: MutableState<TabItem> = rememberSaveable { mutableStateOf(TabItem.HOME) },
    userViewModel: UserViewModel = koinInject(),
    authViewModel: AuthViewModel = koinInject(),
    chatViewModel: ChatViewModel = koinInject(),
    pinboardViewModel: PinboardViewModel = koinInject()
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                TabItem.entries.forEach { tabItem ->
                    NavigationBarItem(
                        selected = selectedTab.value == tabItem,
                        onClick = {
                            selectedTab.value = tabItem
                            navController.navigate(tabItem.route) {
                                popUpTo(DashboardRoute) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = tabItem.tabIcon,
                                contentDescription = "TabItem",
                                tint = if (selectedTab.value == tabItem) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.6f
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = DashboardRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<AuthRoute> {
                AuthScreen(onLoginSuccess = { navController.navigate(DashboardRoute) })
            }

            composable<DashboardRoute> {
                HomeScreen()
            }

            composable<BoulderRoute> {
                RouteScreen(navController = navController)
            }

            composable<CollectionRoute> {
                SettingsScreen()
            }

            composable<OwnProfileRoute> {
                val userId = authViewModel.userId.collectAsState().value
                val user = userViewModel.userState.collectAsState().value

                LaunchedEffect(userId) {
                    if (userId != null) {
                        userViewModel.loadUser(userId)
                    }
                }

                if (user != null) {
                    OwnProfileScreen(
                        onImageUpload = { uri ->
                            userViewModel.uploadProfileImage(userId!!, uri)
                        },
                        onLogout = {
                            authViewModel.signOut()
                            navController.navigate(AuthRoute) {
                                popUpTo(DashboardRoute) { inclusive = true }
                            }
                        }
                    )
                }
            }

            composable<UserProfileRoute> { backStackEntry ->
                val viewedUserId = backStackEntry.toRoute<UserProfileRoute>().userId

                LaunchedEffect(viewedUserId) {
                    userViewModel.loadUser(viewedUserId)
                }

                val currentUserIdState = authViewModel.userId.collectAsState()
                val userState = userViewModel.userState.collectAsState()
                val isLoadingState = userViewModel.isLoading.collectAsState()

                val currentUserId = currentUserIdState.value
                val user = userState.value
                val isLoading = isLoadingState.value

                when {
                    isLoading -> {
                        CircularProgressIndicator()
                    }

                    user != null && currentUserId != null -> {
                        UserProfileScreen(
                            user = user,
                            onStartChat = { targetUserDto ->
                                val targetUserId = targetUserDto.uid

                                if (currentUserId != null && targetUserId != null) {
                                    navController.navigate(
                                        ChatRoute(
                                            currentUserId,
                                            targetUserId
                                        )
                                    )
                                } else {

                                }
                            }
                        )
                    }

                    else -> {
                        Text("Fehler beim Laden des Profils")
                    }
                }
            }

            composable<BoulderDetailRoute> { backStackEntry ->
                val routeArgs = backStackEntry.toRoute<BoulderDetailRoute>()
                RouteDetailScreen(
                    onBackClick = { navController.popBackStack() },
                    routeId = routeArgs.routeId,
                    navController = navController,
                    selectedTab = selectedTab
                )
            }

            composable<ChatRoute> { backStackentry ->
                val routeArgs = backStackentry.toRoute<ChatRoute>()
                ChatScreen(
                    currentUserId = routeArgs.currentUserId,
                    targetUserId = routeArgs.targetUserId,
                    navController = navController
                )
            }

            composable<CommunityRoute> {
                CommunityScreen(
                    userViewModel = userViewModel,
                    chatViewModel = chatViewModel,
                    onUserClick = { user ->
                        navController.navigate(UserProfileRoute(
                            userId = user.uid ?: ""
                        ))
                    },
                    navController = navController
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    AppStart()
}