package com.emc.moodmingle.data.firebase.model.user

import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase

data class Repost(
    val post: PostEntityFirebase? = null,
    val videoUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
