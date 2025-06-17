package com.example.plauenblod.component.routes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.example.plauenblod.model.Difficulty
import com.example.plauenblod.model.HoldColor
import com.example.plauenblod.model.LabeledEnum
import com.example.plauenblod.extension.toColor

@Composable
fun <T> DropdownSelector(
    label: String,
    options: List<T>,
    selected: T?,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(vertical = 4.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 56.dp)
                ,
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(MaterialTheme.colorScheme.outline)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val selectedColor = when(selected) {
                        is HoldColor -> selected.toColor()
                        is Difficulty -> selected.toColor()
                        else -> null
                    }
                    if (selectedColor != null) {
                        Canvas(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(12.dp)
                        ) {
                            drawCircle(color = selectedColor)
                        }
                    }
                    Text(
                        text = (selected as? LabeledEnum)?.label ?: selected.toString(),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val optionColor = when (option) {
                                    is HoldColor -> option.toColor()
                                    is Difficulty -> option.toColor()
                                    else -> null
                                }
                                if (optionColor != null) {
                                    Canvas(
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .size(12.dp)
                                    ) {
                                        drawCircle(color = optionColor)
                                    }
                                }
                                Text((option as? LabeledEnum)?.label ?: option.toString())
                            }
                        },
                        onClick = {
                            onSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
