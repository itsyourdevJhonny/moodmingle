package com.emc.moodmingle.domain.local.model.hide

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hide")
data class HideEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userUid: String,
    val postId: Int,
    val time: Long
)