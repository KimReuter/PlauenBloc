package com.example.plauenblod.component

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.plauenblod.screen.AuthScreen
import com.example.plauenblod.screen.AppStart
import com.example.plauenblod.screen.SplashScreen
import com.example.plauenblod.viewmodel.AuthViewModel
import org.koin.compose.koinInject

@Composable
fun PlauenBlocRoot(
    viewModel: AuthViewModel = koinInject()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isInitialized by viewModel.isInitialized.collectAsState()

    LaunchedEffect(isInitialized) {
        Log.d("PlauenBloc", "SplashScreen shown: ${!isInitialized}")
    }

    when {
        !isInitialized -> {
            SplashScreen()
        }
        isLoggedIn -> {
            AppStart()
        }
        else -> {
            AuthScreen(
                onLoginSuccess = {
                    Log.d("PlauenBloc", "Login erfolgreich")
                }
            )
        }
    }
}