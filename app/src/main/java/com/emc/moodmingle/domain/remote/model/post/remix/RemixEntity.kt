package com.emc.moodmingle.domain.remote.model.post.remix

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class RemixEntity(
    val id: String = "",
    val userId: String = "",
    val inspirerId: String = "",
    val hashtag: String = "",
    val caption: String = "",
    val description: String = "",
    val mood: Mood = Mood(),
    val color: String = "",
    val fontStyle: String = "",
    val textAlignment: String = "",
    val timestamp: Long = System.currentTimeMillis(),

    val reactorIds: List<String> = emptyList(),
    val dislikerIds: List<String> = emptyList(),
    val comments: List<RemixEntityComment> = emptyList(),
    val remixes: List<RemixEntityRemix> = emptyList(),
)

@Parcelize
data class Mood(
    val emoji: String = "",
    val description: String = ""
) : Parcelable

data class RemixEntityRemix(
    val userId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class RemixEntityComment(
    val userId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
