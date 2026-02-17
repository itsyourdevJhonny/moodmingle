package com.emc.moodmingle.data.firebase.model.post.dailymood.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReplayLimit(
    val type: ReplayLimitType = ReplayLimitType.UNLIMITED,
    val replayedUsers: List<ReplayedUser> = emptyList(),
    val customLimit: Int = 2,
) : Parcelable

@Parcelize
data class ReplayedUser(
    val userId: String = "",
    val replayCount: Long = 0
): Parcelable

enum class ReplayLimitType {
    UNLIMITED,
    ONCE,
    CUSTOM
}