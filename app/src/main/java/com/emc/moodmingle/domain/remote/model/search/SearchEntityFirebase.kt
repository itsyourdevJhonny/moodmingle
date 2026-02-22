package com.emc.moodmingle.domain.remote.model.search

import com.google.firebase.firestore.DocumentId

data class SearchEntityFirebase(
    @DocumentId
    val id: String = "",
    val searcherId: String = "",
    val userUid: String = "",
    val username: String = "",
    val time: Long = System.currentTimeMillis()
)