package com.example.plauenblod.component.authentication

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
fun PasswordCriterion(text: String, passed: Boolean) {
    val color = if (passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    Text("• $text", color = color, fontSize = 12.sp)
}