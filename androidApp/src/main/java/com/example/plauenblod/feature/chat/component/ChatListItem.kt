package com.example.plauenblod.feature.chat.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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

@Composable
fun ChatListItem(
    userName: String,
    lastMessage: String,
    onClick: () -> Unit,
    profileImageUrl: String?
    ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
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

        Column(
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text(userName, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(lastMessage, style = MaterialTheme.typography.bodyMedium)
        }
    }
}