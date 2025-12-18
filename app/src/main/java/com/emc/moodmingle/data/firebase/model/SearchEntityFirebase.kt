package com.emc.moodmingle.data.firebase.model

import com.google.firebase.firestore.DocumentId

data class SearchEntityFirebase(
    @DocumentId
    val id: String = "",
    val searcherId: String = "",
    val userUid: String = "",
    val username: String = "",
    val time: Long = System.currentTimeMillis()
)
