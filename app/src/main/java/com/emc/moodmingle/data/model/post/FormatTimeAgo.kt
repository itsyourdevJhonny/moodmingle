package com.emc.moodmingle.data.model.post

fun formatTimeAgo(time: Long): String {
    val currentTime = System.currentTimeMillis()
    val diff = currentTime - time

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val weeks = days / 7
    val months = days / 30
    val years = days / 365

    return when {
        years > 0 -> "${years}y ago"
        months > 0 -> "${months}m ago"
        weeks > 0 -> "${weeks}w ago"
        days > 0 -> "${days}d ago"
        hours > 0 -> "${hours}h ago"
        minutes > 0 -> "${minutes}m ago"
        else -> "${seconds}s ago"
    }
}