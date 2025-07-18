package com.example.plauenblod.feature.chat.component

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plauenblod.feature.chat.model.Message
import com.example.plauenblod.feature.route.model.Route
import com.example.plauenblod.feature.routeCollection.model.RouteCollection

@Composable
fun MessagesList(
    messages: List<Message>,
    currentUserId: String,
    onRouteClick: (String) -> Unit,
    onCollectionClick: (String) -> Unit,
    sharedRoutes: Map<String, Route>,
    sharedCollections: Map<String, RouteCollection>,
    modifier: Modifier = Modifier,
    onLongClick: (Message) -> Unit,
    selectedMessage: Message?
) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LazyColumn(
        modifier = modifier
            .padding(horizontal = 8.dp),
        state = listState,
        reverseLayout = false
    ) {
        items(messages) { message ->
            val isOwnMessage = message.senderId == currentUserId
            val isSelected = selectedMessage?.id == message.id

            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = { message.routeId?.let { onRouteClick(it) } },
                        onLongClick = { onLongClick(message) }
                    ),
                contentAlignment = if (isOwnMessage) Alignment.CenterEnd else Alignment.CenterStart
            ) {
                Surface(
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.secondaryContainer
                        isOwnMessage -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.surface
                    },
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = if (isSelected) 4.dp else 2.dp
                ) {
                    when {
                        message.routeId != null -> {
                            val route = sharedRoutes[message.routeId]
                            if (route != null) {
                                SharedPreviewCard(
                                    title         = route.name,
                                    details       = listOf(
                                        "Schwierigkeit: ${route.difficulty}",
                                        "Erstellt von: ${route.setter}"
                                    ),
                                    stats         = emptyList(),
                                    sharedMessage = message.messageText,
                                    onClick       = { onRouteClick(route.id) }
                                )
                            } else {
                                Text(
                                    text = "⏳ Route wird geladen...",
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .widthIn(max = 280.dp)
                                )
                            }
                        }

                        message.collectionId != null -> {
                            val collection = sharedCollections[message.collectionId]
                            if (collection != null) {
                                SharedPreviewCard(
                                    title         = collection.name,
                                    details       = listOfNotNull(collection.description),
                                    stats         = listOf(
                                        "Routen" to collection.routeCount.toString(),
                                        "Likes"  to collection.likesCount.toString()
                                    ),
                                    sharedMessage = message.messageText,
                                    onClick       = { onCollectionClick(collection.id) }
                                )
                            } else {
                                Text(
                                    text = "⏳ Sammlung wird geladen…",
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .widthIn(max = 280.dp)
                                    )
                            }
                        }

                        else -> {
                            Text(
                                text = message.messageText.orEmpty(),
                                modifier = Modifier
                                    .padding(12.dp)
                                    .widthIn(max = 280.dp),
                                color = if (isOwnMessage) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                }


                message.reactions?.values?.distinct()?.takeIf { it.isNotEmpty() }
                    ?.let { emojiList ->
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 8.dp, y = 8.dp)
                                .padding(end = 16.dp)
                        ) {
                            emojiList.forEach { emoji ->
                                Text(
                                    text = emoji,
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                                )
                            }
                        }
                    }
            }
        }
    }
}