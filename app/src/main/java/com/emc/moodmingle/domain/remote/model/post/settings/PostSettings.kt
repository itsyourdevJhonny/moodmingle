package com.emc.moodmingle.domain.remote.model.post.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.time.LocalDate

@Parcelize
data class PostSettings(
    val audience: @RawValue Any = "Public",
    val activity: String = "",
    val postSchedule: PostSchedule? = null,
    val temporary: PostTemporary = PostTemporary(),
    val pinned: Boolean = false,
    val commentReactionVisibility: PostCommentReactionVisibility = PostCommentReactionVisibility(),
    val filteredKeywords: List<String> = emptyList(),
    val sensitiveFlagEnabled: Boolean = false,
    val blockedUserIds: List<String> = emptyList()
) : Parcelable

@Parcelize
data class PostSchedule(
    val creationTime: Long = System.currentTimeMillis(),
    val expirationDate: LocalDate = LocalDate.now(),
    val expirationTime: Long = 0L
) : Parcelable

@Parcelize
data class PostTemporary(
    val enabled: Boolean = false,
    val savedToArchive: Boolean = false
) : Parcelable

@Parcelize
data class PostCommentReactionVisibility(
    val commentEnabled: Boolean = true,
    val reactionEnabled: Boolean = true,
    val selectedUserIds: List<String> = emptyList()
) : Parcelable
