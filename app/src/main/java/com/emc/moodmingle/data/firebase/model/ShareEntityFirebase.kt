package com.emc.moodmingle.data.firebase.model

import com.google.firebase.firestore.DocumentId

/**
 * Firebase version of ShareEntity for Firestore
 */
data class ShareEntityFirebase(
    @DocumentId
    val id: String = "",
    val userUid: String = "",
    val postId: String = "",
    val time: Long = 0L
)
