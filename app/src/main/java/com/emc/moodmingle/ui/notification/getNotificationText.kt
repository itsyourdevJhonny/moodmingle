package com.emc.moodmingle.ui.notification

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