package com.example.plauenblod.component.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.plauenblod.android.R

@Composable
fun FirstHallMapScreen() {
    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.boulderhalle_grundriss_vordere_halle),
            contentDescription = "Boulderhalle Draufsicht",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        // Hier kommen später die Boulder-Punkte als Overlay drauf
    }
}