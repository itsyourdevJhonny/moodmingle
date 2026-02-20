package com.emc.moodmingle.domain.local.model.post.user

import com.emc.moodmingle.domain.remote.model.post.normal.PostEntityFirebase
import com.emc.moodmingle.domain.remote.model.post.normal.ShareEntityFirebase


data class CombinedPost(
    val id: String,
    val type: PostType,
    val postEntity: PostEntityFirebase?,
    val shareEntity: ShareEntityFirebase?,
    val createdAt: Long
)

enum class PostType {
    USER_POST,
    SHARED_POST,
    REMIX_POST,
    NORMAL_POST
}
