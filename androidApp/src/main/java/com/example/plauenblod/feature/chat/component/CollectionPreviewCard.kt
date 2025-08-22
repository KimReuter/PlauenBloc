package com.example.plauenblod.feature.chat.component

import androidx.compose.runtime.Composable


@Composable
fun CollectionPreviewCard(
    collection: com.example.plauenblod.feature.routeCollection.model.RouteCollection,
    sharedMessage: String?,
    onCollectionClick: () -> Unit
) {
    SharedPreviewCard(
        title = collection.name,
        details = listOf(collection.description ?: "Keine Beschreibung"),
        stats = listOf(
            "Routen" to collection.routeCount.toString(),
            "Likes" to collection.likesCount.toString()
        ),
        sharedMessage = sharedMessage,
        onClick = onCollectionClick
    )
}