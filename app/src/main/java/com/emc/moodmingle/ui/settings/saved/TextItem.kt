package com.emc.moodmingle.ui.settings.saved

import androidx.compose.runtime.Composable
import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.ui.post.text.ExpandableAutoDetectClickableText
import com.emc.moodmingle.ui.theme.Typography

@Composable
fun TextItem(post: PostEntityFirebase) {
    ExpandableAutoDetectClickableText(
        fullText = post.description,
        style = Typography.bodySmall,
        hasPadding = true
    )
}