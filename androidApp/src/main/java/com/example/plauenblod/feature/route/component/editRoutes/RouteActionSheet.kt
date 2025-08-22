package com.example.plauenblod.feature.route.component.editRoutes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plauenblod.feature.route.model.Route
import com.example.plauenblod.feature.auth.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteActionSheet(
    route: Route,
    userRole: UserRole,
    onDismiss: () -> Unit,
    onEdit: (Route) -> Unit,
    onDelete: (Route) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Aktionen für: ${route.name}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (userRole == UserRole.OPERATOR) {
                Button(
                    onClick = {
                        onEdit(route)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Bearbeiten")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onDelete(route)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Löschen")
                }
            }
        }
    }
}