package com.emc.moodmingle.data.model.post

import androidx.room.Embedded
import androidx.room.Relation
import com.emc.moodmingle.data.model.UserEntity

data class PostWithUser(
    @Embedded val post: PostEntity,

    @Relation(
        parentColumn = "userId",
        entityColumn = "uid"
    )
    val user: UserEntity
)
