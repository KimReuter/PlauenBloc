package com.example.plauenblod.feature.authentication.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.example.plauenblod.feature.authentication.component.AnimatedLogo
import com.example.plauenblod.feature.authentication.component.ResetPasswordDialog
import com.example.plauenblod.feature.authentication.component.SignInSection
import com.example.plauenblod.feature.authentication.component.SignUpSection
import com.example.plauenblod.feature.auth.AuthResult
import com.example.plauenblod.feature.auth.viewmodel.AuthViewModel
import org.koin.compose.koinInject

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = koinInject(),
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit
) {
    var signInOrSignUp by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var resetSuccess by remember { mutableStateOf<String?>(null) }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedLogo(modifier = Modifier)

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
                                when (result) {
                                    is AuthResult.Success -> {
                                        errorMessage = null
                                        onLoginSuccess
                                    }
                                    is AuthResult.Error -> errorMessage = result.error.message
                                }
                            }
                        },
                        onForgotPasswordClick = { showResetDialog = true }
                    )

                    if (showResetDialog) {
                        ResetPasswordDialog(
                            email = resetEmail,
                            onEmailChange = { resetEmail = it },
                            onDismiss = { showResetDialog = false },
                            onSend = {
                                viewModel.sendPasswordReset(resetEmail) { result ->
                                    resetSuccess = result.getOrNull()?.let {
                                        "E-Mail zum Zurücksetzen wurde gesendet."
                                    } ?: "Fehler beim Versenden der E-Mail."
                                    showResetDialog = false
                                }
                            }
                        )
                    }
                } else {
                    SignUpSection(
                        onRegisterClick = { userName, email, password ->
                            viewModel.signUp(userName, email, password) { result ->
                                when (result) {
                                    is AuthResult.Success -> {
                                        errorMessage = null
                                        onLoginSuccess()
                                    }
                                    is AuthResult.Error -> errorMessage = result.error.message
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
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
