package com.emc.moodmingle.data.firebase.model.favorites

import com.google.firebase.firestore.DocumentId

data class FavoritesEntityFirebase(
    @DocumentId
    val id: String = "",
    val userUid: String = "",
    val postId: String = "",
    val time: Long = 0L,
    val pinned: Boolean = false
)