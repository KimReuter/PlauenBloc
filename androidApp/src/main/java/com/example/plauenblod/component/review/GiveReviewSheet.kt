package com.example.plauenblod.component.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plauenblod.component.routes.createRoute.DropdownSelector
import com.example.plauenblod.model.RouteReview
import com.example.plauenblod.model.routeProperty.Difficulty
import com.example.plauenblod.viewmodel.AuthViewModel
import kotlinx.datetime.LocalDate
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiveReviewSheet(
    routeId: String,
    perceivedDifficulty: Difficulty,
    onDifficultyChange: (Difficulty) -> Unit,
    commentText: String,
    onCommentChange: (String) -> Unit,
    rating: Int,
    onRatingChange: (Int) -> Unit,
    completed: Boolean,
    onCompletedChange: (Boolean) -> Unit,
    selectedDate: LocalDate?,
    onDateChange: (LocalDate?) -> Unit,
    attempts: Int,
    onAttemptsChange: (Int) -> Unit,
    onSubmit: (RouteReview) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean
) {
    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Rezension abgeben",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            (1..5).forEach { index ->
                IconButton(onClick = { onRatingChange(index) }) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "$index Sterne",
                        tint = if (index <= rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.3f
                        )
                    )
                }
            }
        }

        Divider()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Route abgeschlossen")

            Switch(
                checked = completed,
                onCheckedChange = { onCompletedChange(it) }
            )
        }

        Divider()

        Text("Anzahl der Versuche:")

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (attempts > 1) onAttemptsChange(attempts - 1) }) {
                Icon(Icons.Default.Remove, contentDescription = "Minus")
            }
            Text(attempts.toString())
            IconButton(onClick = { onAttemptsChange(attempts + 1) }) {
                Icon(Icons.Default.Add, contentDescription = "Plus")
            }
        }

        Divider()

        DropdownSelector(
            label = "Geschätzte Schwierigkeit",
            options = Difficulty.values().toList(),
            selected = perceivedDifficulty,
            onSelected = onDifficultyChange
        )

        Divider()

        OutlinedTextField(
            value = commentText,
            onValueChange = { onCommentChange(it) },
            label = { Text("Sag uns deine Meinung!") },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Divider()

        DatePickerDropdown(
            selectedDate = selectedDate?.let {
                java.time.LocalDate.of(it.year, it.monthNumber, it.dayOfMonth)
            },
            onDateSelected = { javaLocalDate ->
                val kxLocalDate = LocalDate(
                    year = javaLocalDate.year,
                    monthNumber = javaLocalDate.monthValue,
                    dayOfMonth = javaLocalDate.dayOfMonth
                )
                onDateChange(kxLocalDate)
            }
        )

        Button(
            onClick = {
                val review = RouteReview(
                    routeId = routeId,
                    stars = rating,
                    comment = commentText,
                    completed = completed,
                    completionDate = selectedDate?.toString(),
                    attempts = attempts,
                    perceivedDifficulty = perceivedDifficulty
                )
                onSubmit(review)
            },
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("Speichern")
        }
    }
}
