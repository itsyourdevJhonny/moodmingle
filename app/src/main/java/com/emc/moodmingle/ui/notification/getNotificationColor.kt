package com.emc.moodmingle.ui.notification

import androidx.compose.ui.graphics.Color
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.ui.theme.SecondaryDark

fun getNotificationColor(type: String?): Color {
    return when (type) {
        "POST_CHAT" -> Color.Blue
        "SHARE" -> Color.Cyan.copy(alpha = 0.6f)
        "COMMENT" -> Color.Green.copy(alpha = 0.6f)
        "SAVE" -> Color.Yellow
        "FOLLOWED" -> PurplePrimary.copy(alpha = 0.6f)
        "UNFOLLOWED" -> Color.White.copy(alpha = 0.6f)
        "SUPPORTED" -> Color.Green.copy(alpha = 0.6f)
        "UNSUPPORTED" -> Color.White.copy(alpha = 0.6f)
        else -> Color.Red
    }
}