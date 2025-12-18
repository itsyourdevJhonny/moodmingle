package com.emc.moodmingle.data.firebase.model.saved

data class CollectionEntityFirebase(
    val id: String = "",
    val userUid: String = "",
    val saveIds: List<String> = emptyList(),
    val name: String = "",
    val time: Long = 0L
)