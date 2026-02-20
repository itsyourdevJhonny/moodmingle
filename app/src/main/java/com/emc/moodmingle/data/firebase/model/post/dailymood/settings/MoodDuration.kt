package com.emc.moodmingle.data.firebase.model.post.dailymood.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class MoodDurationType {
    HOURS_24,
    HOURS_6,
    DAYS_3,
    DAYS_7,
    FOREVER,
    CUSTOM
}

@Parcelize
data class MoodDuration(
    val type: MoodDurationType = MoodDurationType.HOURS_24,
    val customHours: Int? = null
) : Parcelable
