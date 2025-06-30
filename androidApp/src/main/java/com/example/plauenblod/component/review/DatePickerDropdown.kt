package com.example.plauenblod.component.review

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun DatePickerDropdown(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    var isMenuOpen by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = selectedDate?.toString() ?: "Datum auswählen",
            modifier = Modifier
                .clickable { isMenuOpen = true }
                .padding(8.dp)
        )

        DropdownMenu(
            expanded = isMenuOpen,
            onDismissRequest = { isMenuOpen = false }
        ) {
            listOf(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)
            ).forEach { date ->
                DropdownMenuItem(
                    text = { Text(date.toString()) },
                    onClick = {
                        onDateSelected(date)
                        isMenuOpen = false
                    }
                )
            }
        }
    }
}