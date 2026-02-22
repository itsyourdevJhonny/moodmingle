package com.emc.moodmingle.domain.remote.model.user

import com.emc.moodmingle.domain.remote.model.post.normal.PostEntityFirebase

data class Repost(
    val post: PostEntityFirebase? = null,
    val videoUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
