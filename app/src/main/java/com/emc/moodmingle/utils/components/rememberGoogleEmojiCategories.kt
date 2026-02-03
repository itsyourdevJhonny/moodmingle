package com.emc.moodmingle.utils.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.vanniktech.emoji.google.GoogleEmojiProvider
import kotlin.text.orEmpty

@Composable
fun rememberGoogleEmojiCategories(): Map<String, List<String>> {
    val provider = remember { GoogleEmojiProvider() }

    return remember {
        provider.categories.associate {
            it.categoryNames["en"].orEmpty() to it.emojis.map { emoji -> emoji.unicode }
        }
    }
}