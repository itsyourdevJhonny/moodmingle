package com.emc.moodmingle.data.firebase.model.notification

import com.google.firebase.firestore.DocumentId

data class NotificationEntity(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val entityId: String = "",
    val users: List<String> = emptyList(),
    val type: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val read: Boolean = false,
    val pinned: Boolean = false
)