package com.emc.moodmingle.data.firebase.model

import com.emc.moodmingle.data.model.post.ReactionType
import com.google.firebase.firestore.DocumentId

data class ReactionEntityFirebase(
    @DocumentId
    val id: String = "",
    val postId: String = "",
    val reactorId: String = "",
    val reactionType: String = "HEART"
)
