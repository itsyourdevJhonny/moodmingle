package com.emc.moodmingle.utils.text

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimerFormatter {
    fun formatTimestampToAmPm(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatFullDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}