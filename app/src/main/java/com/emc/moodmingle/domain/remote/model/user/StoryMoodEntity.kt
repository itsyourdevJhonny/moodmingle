package com.emc.moodmingle.domain.remote.model.user

data class StoryMoodEntity(
    val id: String,
    val userId: String,
    val mood: String,
    val description: String,
    val musicName: String,
    val musicArtist: String,
    val musicUrl: String,
    val createdAt: Long = System.currentTimeMillis(),
    val expired: Boolean = false
)