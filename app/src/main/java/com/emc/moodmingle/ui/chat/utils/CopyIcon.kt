package com.emc.moodmingle.ui.chat.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.chat.ChatMessage
import com.emc.moodmingle.utils.copyText
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun CopyIcon(chatMessage: ChatMessage) {
    val context = LocalContext.current

    Icon(
        painter = painterResource(R.drawable.copy),
        contentDescription = "Copy",
        modifier = Modifier
            .size(16.dp)
            .graphicsLayer(alpha = 0.99f)
            .drawGradient()
            .clickable { copyText(chatMessage.message, context) }
    )
}