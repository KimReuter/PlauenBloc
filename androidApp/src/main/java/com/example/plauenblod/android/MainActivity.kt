package com.example.plauenblod.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.plauenblod.Greeting
import com.example.plauenblod.android.plauenBlocTheme.PlauenBlocTheme
import com.example.plauenblod.component.PlauenBlocRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlauenBlocTheme {
                PlauenBlocRoot()
            }
        }
    }
}

