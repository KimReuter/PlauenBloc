package com.example.plauenblod.feature.chat.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import com.example.plauenblod.feature.user.model.UserDto

@Composable
fun UserListItem(
    user: UserDto,
    onProfileClick: () -> Unit,
    onStartChatClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProfileClick() }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = user.profileImageUrl,
                contentDescription = "Profilbild von ${user.userName}",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                fallback = painterResource(R.drawable.placeholderprofileimage),
                error = painterResource(R.drawable.placeholderprofileimage)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user.userName ?: "Unbekannt", style = MaterialTheme.typography.titleMedium)
                user.bio?.let {
                    Text(text = it, style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(
                onClick = onStartChatClick
            ) {
                Icon(
                    imageVector = Icons.Default.ChatBubble,
                    contentDescription = "Chat starten",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Divider(modifier = Modifier.padding(top = 12.dp))
    }
}