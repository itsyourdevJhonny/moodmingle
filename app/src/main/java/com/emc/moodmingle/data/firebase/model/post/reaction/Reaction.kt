package com.emc.moodmingle.data.firebase.model.post.reaction

import com.emc.moodmingle.data.model.post.user.PostType

data class Reaction(
    val id: String,
    val entityId: String,
    val reactorIds: List<String>,
    val type: PostType = PostType.NORMAL_POST
)
