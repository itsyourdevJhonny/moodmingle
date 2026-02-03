package com.emc.moodmingle.data.firebase.model.post.dailymood

import android.os.Parcelable
import com.emc.moodmingle.api.soundcloud.model.TrackResponse
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
    val location: String = ""
)

@Parcelize
data class MusicTrack(
    val id: Long,
    val title: String,
    val artist: String,
    @Json(name = "artworkUrl") val artworkUrl: String?,
    @Json(name = "streamUrl") val streamUrl: String?,
    val duration: Long
) : Parcelable
