package com.example.plauenblod.feature.chat.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.plauenblod.android.R
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

fun formatChatTimestamp(instant: Instant): String {
    val now = Clock.System.now()
    val tz = TimeZone.currentSystemDefault()
    val messageDate = instant.toLocalDateTime(tz).date
    val nowDate = now.toLocalDateTime(tz).date

    return when {
        messageDate == nowDate -> {
            val time = instant.toLocalDateTime(tz).time
            "%02d:%02d".format(time.hour, time.minute)
        }
        messageDate == nowDate.minus(DatePeriod(days = 1)) -> {
            "Gestern"
        }
        messageDate > nowDate.minus(DatePeriod(days = 6)) -> {
            val dayOfWeekGerman = when (messageDate.dayOfWeek.name) {
                "MONDAY" -> "Montag"
                "TUESDAY" -> "Dienstag"
                "WEDNESDAY" -> "Mittwoch"
                "THURSDAY" -> "Donnerstag"
                "FRIDAY" -> "Freitag"
                "SATURDAY" -> "Samstag"
                "SUNDAY" -> "Sonntag"
                else -> ""
            }
            dayOfWeekGerman
        }
        else -> {
            "${messageDate.dayOfMonth}.${messageDate.monthNumber}.${messageDate.year}"
        }
    }
}

@Composable
fun ChatListItem(
    userName: String,
    lastMessage: String,
    lastMessageTime: Instant?,
    onClick: () -> Unit,
    profileImageUrl: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = profileImageUrl,
                contentDescription = "Profilbild von $userName",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                fallback = painterResource(R.drawable.placeholderprofileimage),
                error = painterResource(R.drawable.placeholderprofileimage)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(userName, style = MaterialTheme.typography.titleMedium)
                    lastMessageTime?.let {
                        Text(
                            text = formatChatTimestamp(it),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Divider()
    }
}