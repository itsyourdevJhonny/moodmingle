package com.emc.moodmingle.domain.local.model.post

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userUid: String,
    val postId: Int,
    val message: String,
    val time: Long
)