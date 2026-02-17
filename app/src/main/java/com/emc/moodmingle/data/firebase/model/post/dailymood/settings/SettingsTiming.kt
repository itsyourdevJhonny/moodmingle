package com.emc.moodmingle.data.firebase.model.post.dailymood.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class SettingsTiming(
    val type: TimingType = TimingType.AUTO_POST_NOW,
    val scheduledAt: LocalDateTime? = null
) : Parcelable

enum class TimingType {
    AUTO_POST_NOW,
    SCHEDULE,
    MANUAL_ONLY
}