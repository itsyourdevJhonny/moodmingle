package com.emc.moodmingle.data.firebase.model.post.reaction

import com.google.firebase.firestore.DocumentId

data class ReactionEntityFirebase(
    @DocumentId
    val id: String = "",
    val postId: String = "",
    val reactorId: String = "",
    val reactionType: String = "HEART"
)