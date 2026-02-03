package com.emc.moodmingle.api.soundcloud.model

data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val artworkUrl: String?,
    val streamUrl: String,
    val permalinkUrl: String,
    val durationMs: Long
)
