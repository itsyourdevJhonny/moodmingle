package com.emc.moodmingle.domain.remote.model.post.normal

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Represents a comment stored in Firebase Firestore.
 */
data class CommentEntityFirebase(
    @DocumentId
    val id: String = "",
    val userUid: String = "",
    val postId: String = "",
    val message: String = "",
    val time: Timestamp = Timestamp.now()
)
