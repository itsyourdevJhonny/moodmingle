package com.emc.moodmingle.utils.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import com.emc.moodmingle.ui.create.post.hashtag.extractHashtags
import com.emc.moodmingle.ui.theme.Typography

@Composable
fun AnnotatedHashtag(hashtag: String, style: TextStyle = Typography.bodySmall) {
    if (hashtag.isNotBlank()) {
        val hashtags = extractHashtags(hashtag)

        ExpandableAnnotatedText(
            fullText = hashtags.joinToString(" ") { "#${it.replace(" ", "")}" },
            style = style,
            minLines = 1
        )
    }
}