package com.emc.moodmingle.data.firebase.model.post.dailymood

import android.os.Parcelable
import com.emc.moodmingle.data.firebase.model.post.normal.PostDescription
import com.emc.moodmingle.data.firebase.model.remix.Mood
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

data class DailyMoodEntity(
    val id: String = "",
    val mood: Mood = Mood(),
    val description: PostDescription? = null,
    val mediaUrls: List<String> = emptyList(),
    val musicTrack: MusicTrack? = null,
    val location: String = "",
    val text: DailyMoodText = DailyMoodText(),
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)

@Parcelize
data class DailyMoodText(
    val text: String = "",
    val color: String = "",
    val font: String = "",
    val align: String = "",
    val hashtags: List<String> = emptyList(),
    val mentions: List<String> = emptyList()
) : Parcelable

@Parcelize
data class MusicTrack(
    val id: Long,
    val title: String,
    val artist: String,
    @Json(name = "artworkUrl") val artworkUrl: String?,
    @Json(name = "streamUrl") val streamUrl: String?,
    val duration: Long
) : Parcelable
