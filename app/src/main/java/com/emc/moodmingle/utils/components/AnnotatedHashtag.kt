package com.emc.moodmingle.utils.components

import androidx.compose.runtime.Composable
import com.emc.moodmingle.ui.create.post.hashtag.extractHashtags

@Composable
fun AnnotatedHashtag(hashtag: String) {
    if (hashtag.isNotBlank()) {
        val hashtags = extractHashtags(hashtag)

        ExpandableAnnotatedText(
            fullText = hashtags.joinToString(" ") { "#${it.replace(" ", "")}" },
            minLines = 1
        )
    }
}