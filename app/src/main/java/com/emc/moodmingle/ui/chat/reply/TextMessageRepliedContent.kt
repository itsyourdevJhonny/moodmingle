package com.emc.moodmingle.ui.chat.reply

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.chat.ChatMessage
import com.emc.moodmingle.ui.theme.BrushSecondaryDarkGradient
import com.emc.moodmingle.ui.theme.BrushSecondaryTertiaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun TextMessageRepliedContent(chatMessage: ChatMessage, isOwn: Boolean) {
    Box(
        modifier = Modifier.background(
            if (isOwn) BrushSecondaryTertiaryGradient else BrushSecondaryDarkGradient,
            RoundedCornerShape(8.dp)
        )
    ) {
        Column {
            Box(
                modifier = Modifier.background(
                    BrushSecondaryDarkGradient,
                    RoundedCornerShape(8.dp)
                )
            ) {
                Text(
                    text = chatMessage.replyMessage,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = GrayTextColor,
                    style = Typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.reply),
                    contentDescription = "Reply",
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawGradient(),
                    tint = Color.White
                )

                Text(
                    text = if (isOwn) "You replied" else "Replied to you",
                    style = Typography.bodySmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.W900,
                        fontStyle = FontStyle.Italic
                    )
                )
            }

            Text(
                text = chatMessage.message,
                color = Color.White,
                modifier = Modifier
                    .padding(start = 8.dp, bottom = 8.dp, end = 8.dp)
                    .fillMaxWidth()
            )
        }
    }
}