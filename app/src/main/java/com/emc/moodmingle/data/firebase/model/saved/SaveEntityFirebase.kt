package com.emc.moodmingle.data.firebase.model.saved

data class SaveEntityFirebase(
    val id: String = "",
    val userUid: String = "",
    val postId: String = "",
    val time: Long = 0L,
    val pinned: Boolean  = false
)