package com.emc.moodmingle.ui.video.comment.media.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.BackIcon

@Composable
fun VideoCommentMediaDialogHeader(commenterUsername: String, onDismiss: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        BackIcon(onClick = onDismiss)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "From ", style = Typography.bodyMedium.copy(color = GrayTextColor))

            Text(
                text = commenterUsername + if (commenterUsername.endsWith("s'")) "s" else "'s",
                style = Typography.bodyLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )
            )

            Text(
                text = " comment",
                maxLines = 1,
                style = Typography.bodyMedium.copy(color = GrayTextColor),
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}