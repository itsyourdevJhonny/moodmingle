package com.emc.moodmingle.data.firebase.model

import com.google.firebase.firestore.DocumentId

data class HideEntityFirebase(
    @DocumentId
    val id: String = "",
    val userUid: String = "",
    val postId: String = "",
    val time: Long = 0L
)
