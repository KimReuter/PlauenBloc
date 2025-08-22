package com.example.plauenblod.feature.authentication.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SignInSection(
    modifier: Modifier = Modifier,
    onLoginClick: (String, String) -> Unit,
    onForgotPasswordClick: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "E-Mail",
            leadingIcon = Icons.Default.Email
        )

        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Passwort",
            leadingIcon = Icons.Default.Lock,
            isPassword = true
        )

        Spacer(modifier = modifier.padding(8.dp))

        Button(
            onClick = { onLoginClick(email, password) }
        ) {
            Text("Einloggen")
        }

        Spacer(modifier = modifier.padding(8.dp))

        TextButton(
            onClick = { onForgotPasswordClick(email) }
        ) {
            Text("Passwort vergessen?")
        }
    }

}
