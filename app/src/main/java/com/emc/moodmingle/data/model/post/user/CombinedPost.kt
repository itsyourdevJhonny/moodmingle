package com.emc.moodmingle.data.model.post.user

import com.emc.moodmingle.data.firebase.model.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.ShareEntityFirebase
import com.emc.moodmingle.data.model.post.PostEntity
import com.emc.moodmingle.data.model.share.ShareEntity


data class CombinedPost(
    val id: String,
    val type: PostType,
    val postEntity: PostEntityFirebase?,
    val shareEntity: ShareEntityFirebase?,
    val createdAt: Long
)

enum class PostType {
    USER_POST,
    SHARED_POST
}
