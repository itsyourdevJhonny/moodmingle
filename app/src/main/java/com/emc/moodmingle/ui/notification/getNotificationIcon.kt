package com.emc.moodmingle.ui.notification

import com.emc.moodmingle.R

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