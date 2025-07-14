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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.plauenblod.android.R
import com.example.plauenblod.feature.routeReview.model.RouteReview
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

@OptIn(kotlin.time.ExperimentalTime::class)
@Composable
fun ReviewItem(
    review: RouteReview,
    backgroundColor: Color,
    currentUserId: String,
    onEdit: (RouteReview) -> Unit,
    onDelete: (RouteReview) -> Unit,
    onUserClick: (String) -> Unit
    ) {
    var menuExpanded by remember { mutableStateOf(false) }
    var textExpanded by remember { mutableStateOf(false) }
    val isLongText = review.comment.length > 90

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(8.dp)
    ) {
        val (profile, textBlock, menu) = createRefs()

        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .constrainAs(profile) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        ) {
            AsyncImage(
                model = review.userProfileImageUrl,
                contentDescription = "Profilbild",
                placeholder = painterResource(R.drawable.placeholderprofileimage),
                error = painterResource(R.drawable.placeholderprofileimage),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clickable { onUserClick(review.userId)}
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = review.userName.ifBlank { "Anonymer Nutzer" },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onUserClick(review.userId)}
                )
                review.timeStamp?.let { instant ->
                    val relativeTime = getRelativeTime(instant)
                    Text(
                        text = relativeTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Column(
            modifier = Modifier.constrainAs(textBlock) {
                top.linkTo(profile.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        ) {
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (textExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (isLongText) {
                Text(
                    text = if (textExpanded) "Weniger anzeigen" else "Mehr anzeigen",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .clickable { textExpanded = !textExpanded }
                        .padding(top = 4.dp)
                )
            }
        }

        if (review.userId == currentUserId) {
            Box(
                modifier = Modifier.constrainAs(menu) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
            ) {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Mehr Optionen")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Bearbeiten") },
                        onClick = {
                            menuExpanded = false
                            onEdit(review)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Löschen") },
                        onClick = {
                            menuExpanded = false
                            onDelete(review)
                        }
                    )
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