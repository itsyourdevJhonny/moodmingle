package com.emc.moodmingle.domain.local.model.post

import androidx.room.Embedded
import androidx.room.Relation
import com.emc.moodmingle.domain.local.model.user.UserEntity

data class PostWithUser(
    @Embedded val post: PostEntity,

    @Relation(
        parentColumn = "userId",
        entityColumn = "uid"
    )
    val user: UserEntity
)
