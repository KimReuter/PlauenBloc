package com.example.plauenblod.feature.chat.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmojiGrid(
    onEmojiSelected: (String) -> Unit
) {
    val emojis = listOf(
        "🧗", "⛷️", "🏔️", "🚴", "🥾", "🧘", "🏕️", "🌄", "🌲", "🌞",
        "😄", "🤣", "😍", "🥹", "😭", "🤯", "😎", "🤩", "🥳", "🙃",
        "🔥", "💪", "🎯", "🚀", "✨", "🎉", "🏅", "🥇", "📈", "❤️",
        "🐒", "🐈", "🦉", "🦄", "🪨", "🎒", "🧤", "🕶️", "🧢",
        "🌈", "🍉", "🍕", "🍪", "🎧", "🎸", "📸", "🎬", "📚", "💡"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(6),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(emojis.size) { index ->
            Text(
                text = emojis[index],
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onEmojiSelected(emojis[index]) }
            )
        }
    }
}