package com.emc.moodmingle.domain.remote.model.post.dailymood

import com.emc.moodmingle.domain.remote.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.domain.remote.model.post.dailymood.settings.MoodDuration
import com.emc.moodmingle.domain.remote.model.post.dailymood.settings.MoodDurationType
import com.emc.moodmingle.domain.remote.model.post.dailymood.settings.TimingType
import java.time.ZoneId

/**
 * Computes expiration timestamp based on mood duration.
 * Returns null if FOREVER.
 */
fun DailyMoodSettings.computeExpiresAt(createdAt: Long): Long? {
    val durationMillis = duration.toMillisOrNull()
    return durationMillis?.let { createdAt + it }
}

/**
 * Returns true if the mood is currently active and not expired.
 */
fun DailyMoodEntity.isActiveAndNotExpired(): Boolean {
    val now = System.currentTimeMillis()

    // -----------------------------
    // 1. Check if already posted
    // -----------------------------
    val timing = settings.timing

    val isPosted = when (timing.type) {
        TimingType.AUTO_POST_NOW -> true

        TimingType.SCHEDULE -> {
            timing.scheduledAt?.let {
                val scheduledMillis = it
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                now >= scheduledMillis
            } ?: false
        }

        TimingType.MANUAL_ONLY -> false
    }

    if (!isPosted) return false

    // -----------------------------
    // 2. Check expiration
    // -----------------------------
    val durationMillis = settings.duration.toMillisOrNull()

    // FOREVER → never expires
    if (durationMillis == null) return true

    val expirationTime = createdAt + durationMillis

    return now < expirationTime
}

/**
 * Converts MoodDuration into milliseconds.
 * FOREVER returns null to indicate no expiration.
 */
fun MoodDuration.toMillisOrNull(): Long? {
    return when (type) {
        MoodDurationType.HOURS_24 -> 24 * 60 * 60 * 1000L
        MoodDurationType.HOURS_6 -> 6 * 60 * 60 * 1000L
        MoodDurationType.DAYS_3 -> 3 * 24 * 60 * 60 * 1000L
        MoodDurationType.DAYS_7 -> 7 * 24 * 60 * 60 * 1000L
        MoodDurationType.FOREVER -> null
        MoodDurationType.CUSTOM -> (customHours ?: 0) * 60 * 60 * 1000L
    }
}