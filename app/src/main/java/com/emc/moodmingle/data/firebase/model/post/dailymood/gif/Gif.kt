package com.emc.moodmingle.data.firebase.model.post.dailymood.gif

data class Gif(
    val url: String = "",
    val type: GifType = GifType.IMAGE,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
)

enum class GifType {
    IMAGE,
    VIDEO
}