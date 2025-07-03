package com.example.plauenblod.component.review

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.plauenblod.component.routes.ColorDot
import com.example.plauenblod.extension.displayName
import com.example.plauenblod.extension.toColor
import com.example.plauenblod.model.Route

@Composable
fun RouteInformation(
    route: Route,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Informationen",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
            color = MaterialTheme.colorScheme.tertiary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "${route.hall.displayName()} - ${route.sector.displayName()}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))
        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Grifffarbe:", style = MaterialTheme.typography.bodyMedium)
            ColorDot(route.holdColor.toColor())
            Text(
                "Schrauber: ${route.setter}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = route.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))
        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        Spacer(modifier = Modifier.height(24.dp))
    }
}