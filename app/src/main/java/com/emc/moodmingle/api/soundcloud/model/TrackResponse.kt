package com.emc.moodmingle.api.soundcloud.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackResponse(
    val id: Long,
    val title: String,
    val artist: String,
    @Json(name = "artworkUrl") val artworkUrl: String?,
    val duration: Long,
    val permalinkUrl: String
) : Parcelable
