package com.emc.moodmingle.data.model.share

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "share")
data class ShareEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userUid: String,
    val postId: Int,
    val time: Long
)