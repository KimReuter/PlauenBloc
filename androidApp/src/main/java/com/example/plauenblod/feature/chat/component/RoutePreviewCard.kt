package com.example.plauenblod.feature.chat.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.plauenblod.feature.route.model.Route

@Composable
fun RoutePreviewCard(
    route: Route,
    sharedMessage: String?,
    onRouteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRouteClick() }
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(route.name, style = MaterialTheme.typography.titleMedium)
            Text("Schwierigkeit: ${route.difficulty}")
            Text("Erstellt von: ${route.setter}")

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            sharedMessage?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}