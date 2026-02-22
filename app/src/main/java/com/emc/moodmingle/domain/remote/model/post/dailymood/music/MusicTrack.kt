package com.emc.moodmingle.domain.remote.model.post.dailymood.music

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicTrack(
    val id: Long,
    val title: String,
    val artist: String,
    @Json(name = "artworkUrl") val artworkUrl: String?,
    @Json(name = "streamUrl") val streamUrl: String?,
    val duration: Long,
) : Parcelable