package com.example.plauenblod.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plauenblod.android.R
import com.example.plauenblod.component.authentication.AuthBackground
import com.example.plauenblod.component.authentication.SignInSection
import com.example.plauenblod.component.authentication.SignUpSection
import com.example.plauenblod.viewmodel.AuthViewModel
import org.koin.compose.koinInject

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = koinInject(),
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit
) {
    var signInOrSignUp by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AuthBackground {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "PlauenBloc",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (signInOrSignUp) {
                    SignInSection(
                        onLoginClick = { email, password ->
                            viewModel.signIn(email, password) { result ->
                                result.onSuccess {
                                    errorMessage = null
                                    onLoginSuccess()
                                }
                                result.onFailure { error ->
                                    errorMessage = error.message ?: "Unbekannter Fehler beim Login"
                                }
                            }
                        }
                    )
                } else {
                    SignUpSection(
                        onRegisterClick = { userName, email, password ->
                            viewModel.signUp(userName, email, password) { result ->
                                result.onSuccess {
                                    errorMessage = null
                                    onLoginSuccess()
                                }
                                result.onFailure { error ->
                                    errorMessage = error.message ?: "Registrierung fehlgeschlagen"
                                }
                            }
                        }
                    )
                }

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, color = Color.Red)
                }
            }

            TextButton(onClick = { signInOrSignUp = !signInOrSignUp }) {
                Text(
                    if (signInOrSignUp) "Noch keinen Account? Registrieren"
                    else "Bereits registriert? Einloggen",
                    color = Color.White
                )
            }
        }
    }
}