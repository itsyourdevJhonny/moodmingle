package com.emc.moodmingle.data.model.post.user

import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.post.ShareEntityFirebase


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
