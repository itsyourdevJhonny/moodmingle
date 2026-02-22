package com.emc.moodmingle.domain.remote.model.post.dailymood.gif

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Gif(
    val url: String = "",
    val type: GifType = GifType.IMAGE,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
) : Parcelable

enum class GifType {
    IMAGE,
    VIDEO
}