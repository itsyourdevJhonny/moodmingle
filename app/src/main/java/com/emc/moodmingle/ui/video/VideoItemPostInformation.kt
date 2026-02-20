package com.emc.moodmingle.ui.video

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.normal.PostEntityFirebase
import com.emc.moodmingle.ui.post.text.ExpandableAutoDetectClickableText
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun VideoItemPostInformation(post: PostEntityFirebase) {
    val informationTypes = listOf(
        R.drawable.hashtag to post.hashtag,
        R.drawable.caption to post.caption,
        R.drawable.description to post.description
    )

    Column {
        informationTypes.forEach { informationType ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(informationType.first),
                    contentDescription = "Icon",
                    modifier = Modifier
                        .size(16.dp)
                        .drawGradient()
                )

                ExpandableAutoDetectClickableText(
                    fullText = informationType.second,
                    style = Typography.bodySmall,
                    hasPadding = false
                )
            }
        }
    }
}