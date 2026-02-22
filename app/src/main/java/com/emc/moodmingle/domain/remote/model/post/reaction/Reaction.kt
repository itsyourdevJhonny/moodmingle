package com.emc.moodmingle.domain.remote.model.post.reaction

import com.emc.moodmingle.domain.local.model.post.user.PostType

data class Reaction(
    val id: String,
    val entityId: String,
    val reactorIds: List<String>,
    val type: PostType = PostType.NORMAL_POST
)
