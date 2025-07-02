package com.example.plauenblod.component.review

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.plauenblod.android.R
import com.example.plauenblod.model.RouteReview
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(kotlin.time.ExperimentalTime::class)
@Composable
fun ReviewItem(
    review: RouteReview,
    backgroundColor: Color,
    currentUserId: String,
    onEdit: (RouteReview) -> Unit,
    onDelete: (RouteReview) -> Unit
    ) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = review.userProfileImageUrl,
                contentDescription = "Profilbild",
                placeholder = painterResource(R.drawable.placeholderprofileimage),
                error = painterResource(R.drawable.placeholderprofileimage),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = review.userName.ifBlank { "Anonymer Nutzer" },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                review.timeStamp?.let { instant ->
                    val relativeTime = getRelativeTime(instant)
                    Text(
                        text = relativeTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = review.comment,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 4
                )
            }
        }

        if (review.userId == currentUserId) {
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Mehr Optionen")
                }
                if (expanded) {
                    Surface(
                        tonalElevation = 4.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.width(IntrinsicSize.Min)
                    ) {
                        Column {
                            Text(
                                text = "Bearbeiten",
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expanded = false
                                        onEdit(review)
                                    }
                                    .padding(12.dp)
                                )
                            Text(
                                text = "Löschen",
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expanded = false
                                        onDelete(review)
                                    }
                                    .padding(12.dp)
                            )
                        }

                    }
                }
            }
        }
    }
}

@OptIn(kotlin.time.ExperimentalTime::class)
fun getRelativeTime(instant: Instant): String {
    val now = Clock.System.now()
    val diff = now - instant

    return when {
        diff.inWholeDays < 1 -> "heute"
        diff.inWholeDays == 1L -> "gestern"
        diff.inWholeDays < 7 -> "vor ${diff.inWholeDays} Tagen"
        else -> {
            val date = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
            "${date.dayOfMonth}.${date.monthNumber}.${date.year}"
        }
    }
}