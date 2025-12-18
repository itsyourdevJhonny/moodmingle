package com.emc.moodmingle.ui.chat.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object ChatTimerFormatter {
    fun dateLabelFor(tsMillis: Long): String {
        val d = Date(tsMillis)
        val today = Calendar.getInstance()
        val cal = Calendar.getInstance().apply { time = d }
        return when {
            isSameDay(today, cal) -> "Today"
            isYesterday(today, cal) -> "Yesterday"
            else -> {
                val fmt = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                fmt.format(d)
            }
        }
    }

    fun isSameDay(a: Calendar, b: Calendar): Boolean {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
    }

    fun isYesterday(today: Calendar, cal: Calendar): Boolean {
        val yesterday = today.clone() as Calendar
        yesterday.add(Calendar.DAY_OF_YEAR, -1)
        return isSameDay(yesterday, cal)
    }

    fun formatChatTimeAgo(timeMillis: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timeMillis

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val months = days / 30
        val years = days / 365

        return when {
            seconds < 60 -> "Just now"
            minutes < 60 -> {
                val m = minutes.toInt()
                "$m minute${if (m != 1) "s" else ""} ago"
            }

            hours < 24 -> {
                val h = hours.toInt()
                "$h hour${if (h != 1) "s" else ""} ago"
            }

            days < 30 -> {
                val d = days.toInt()
                "$d day${if (d != 1) "s" else ""} ago"
            }

            months < 12 -> {
                val m = months.toInt()
                "$m month${if (m != 1) "s" else ""} ago"
            }

            else -> {
                val y = years.toInt()
                "$y year${if (y != 1) "s" else ""} ago"
            }
        }
    }
}
