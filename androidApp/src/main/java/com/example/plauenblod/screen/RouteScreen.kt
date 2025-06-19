package com.example.plauenblod.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.plauenblod.component.ScreenTitle
import com.example.plauenblod.component.map.BoulderSearchBar
import com.example.plauenblod.component.map.FirstHallMapScreen
import com.example.plauenblod.component.map.SecondHallMapScreen
import com.example.plauenblod.component.routes.CreateRouteSheet
import com.example.plauenblod.component.routes.MenuButton
import com.example.plauenblod.extension.hallSection
import com.example.plauenblod.model.HallSection
import com.example.plauenblod.viewmodel.AuthViewModel
import com.example.plauenblod.viewmodel.RouteViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteScreen(
    modifier: Modifier = Modifier,
    routeViewModel: RouteViewModel = koinInject(),
    authViewModel: AuthViewModel = koinInject(),
    navController: NavHostController
) {
    var selectedHall by remember { mutableStateOf("first") }
    var query by remember { mutableStateOf("") }
    var showMap by remember { mutableStateOf(true) }
    val userRole by authViewModel.userRole.collectAsState()
    var showCreateSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val allRoutes by routeViewModel.routes.collectAsState()
    val filteredRoutes = allRoutes.filter {
        it.sector.hallSection() == if (selectedHall == "first") HallSection.FRONT else HallSection.BACK
    }

    Box(
        modifier = modifier
            .padding(16.dp)
    ) {
        ScreenTitle("Routen")

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BoulderSearchBar(
                    query = query,
                    onQueryChanged = { query = it },
                    modifier = Modifier.weight(1f)
                )

                MenuButton(
                    currentUserRole = userRole,
                    onCreateRouteClick = {
                        showCreateSheet = true
                    },
                    onFilterClick = {
                        println("Filter geöffnet")
                    }
                )

            }

            Spacer(modifier = modifier.height(16.dp))

            Row {
                Button(
                    onClick = { selectedHall = "second" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedHall == "second") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("Hintere Halle")
                }

                Spacer(modifier = modifier.width(16.dp))

                Button(
                    onClick = { selectedHall = "first" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedHall == "first") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    ),

                    ) {
                    Text("Vordere Halle")
                }
            }

            if (selectedHall == "first") {
                if (showMap) {
                    FirstHallMapScreen(
                        routes = filteredRoutes,
                        navController = navController
                        )
                } else {
                    //FirstHallListScreen()
                }
            } else {
                if (showMap) {
                    SecondHallMapScreen(
                        routes = filteredRoutes,
                        navController = navController
                    )
                } else {
                    //SecondHallListScreen()
                }
            }

            if (showCreateSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showCreateSheet = false },
                    sheetState = sheetState
                ) {
                    CreateRouteSheet(
                        onDismiss = { showCreateSheet = false }
                    )
                }
            }
        }
    }
}