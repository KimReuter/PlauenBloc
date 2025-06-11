package com.example.plauenblod.component.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plauenblod.android.util.authentication.checkPasswordCriteria

@Composable
fun SignUpSection(
    modifier: Modifier = Modifier,
    onRegisterClick: (String, String, String) -> Unit
) {
    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val passwordCheck = remember(password) { checkPasswordCriteria(password) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthTextField(
            value = userName,
            onValueChange = { userName = it },
            label = "Benutzername",
            leadingIcon = Icons.Default.Person
        )

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
            leadingIcon = Icons.Default.Lock
        )

        Spacer(modifier = modifier.padding(8.dp))

        Column(modifier = Modifier.padding(top = 8.dp)) {
            PasswordCriterion("Mind. 8 Zeichen", passwordCheck.hasMinLength)
            PasswordCriterion("Groß- & Kleinbuchstaben", passwordCheck.hasUpperLowerCase)
            PasswordCriterion("Zahl enthalten", passwordCheck.hasNumber)
            PasswordCriterion("Sonderzeichen", passwordCheck.hasSpecialChar)
        }

        Spacer(modifier = modifier.padding(8.dp))

        Button(
            onClick = { onRegisterClick(userName, email, password) }
        ) {
            Text("Registrieren")
        }
    }
}