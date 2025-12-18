package com.emc.moodmingle.ui.chat.reply

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.data.firebase.model.chat.ChatMessage
import com.emc.moodmingle.ui.theme.BrushSecondaryTertiaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor

@Composable
fun TextReply(chatMessage: ChatMessage, onTextMessageReplied: (Boolean) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = BrushSecondaryTertiaryGradient,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Replying...",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.Red,
                modifier = Modifier.clickable { onTextMessageReplied(false) }
            )
        }

        Text(
            text = chatMessage.message,
            color = GrayTextColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}