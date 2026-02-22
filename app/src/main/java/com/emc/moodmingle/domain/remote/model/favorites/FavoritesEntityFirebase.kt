package com.emc.moodmingle.domain.remote.model.favorites

import com.google.firebase.firestore.DocumentId

data class FavoritesEntityFirebase(
    @DocumentId
    val id: String = "",
    val userUid: String = "",
    val postId: String = "",
    val time: Long = 0L,
    val pinned: Boolean = false
)