package com.emc.moodmingle.ui.notification

import androidx.compose.ui.graphics.Color
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.PurplePrimary

class NotificationUtils {

    fun getNotificationColor(type: String?): Color {
        return when (type) {
            "POST_CHAT" -> Color.Companion.Blue
            "SHARE" -> Color.Companion.Cyan.copy(alpha = 0.6f)
            "COMMENT" -> Color.Companion.Green.copy(alpha = 0.6f)
            "SAVE" -> Color.Companion.Yellow
            "FOLLOWED" -> PurplePrimary.copy(alpha = 0.6f)
            "UNFOLLOWED" -> Color.Companion.White.copy(alpha = 0.6f)
            "SUPPORTED" -> Color.Companion.Green.copy(alpha = 0.6f)
            "UNSUPPORTED" -> Color.Companion.White.copy(alpha = 0.6f)
            else -> Color.Companion.Red
        }
    }

    fun getNotificationText(type: String?): String {
        return when (type) {
            "POST_CHAT" -> "messaged on your post"
            "SHARE" -> "shared your post"
            "COMMENT" -> "commented on your post"
            "SAVE" -> "saved your post"
            "FOLLOWED" -> "followed you"
            "UNFOLLOWED" -> "unfollowed you"
            "SUPPORTED" -> "supported you"
            "UNSUPPORTED" -> "unsupported you"
            else -> "reacted to your post"
        }
    }

    fun getNotificationIcon(type: String?): Int {
        return when (type) {
            "POST_CHAT" -> R.drawable.chat
            "SHARE" -> R.drawable.share
            "COMMENT" -> R.drawable.comment
            "SAVE" -> R.drawable.save_post
            "FOLLOWED" -> R.drawable.following
            "UNFOLLOWED" -> R.drawable.follow
            "SUPPORTED" -> R.drawable.supporting
            "UNSUPPORTED" -> R.drawable.supporter
            else -> R.drawable.love
        }
    }
}