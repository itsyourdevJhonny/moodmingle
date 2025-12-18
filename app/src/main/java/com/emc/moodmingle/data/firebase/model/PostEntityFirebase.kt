package com.emc.moodmingle.data.firebase.model

data class PostEntityFirebase(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val avatarUrl: String = "",
    val mood: String = "",
    val moodEmoji: String = "",
    val hashtag: String = "",
    val caption: String = "",
    val description: String = "",
    val timeAgo: Long = 0L,
    val comments: Long = 0,
    val likes: Long = 0,
    val shares: Long = 0,
    val type: String = "TEXT",
    val urls: /*String = ""*/ List<String> = emptyList()
)
