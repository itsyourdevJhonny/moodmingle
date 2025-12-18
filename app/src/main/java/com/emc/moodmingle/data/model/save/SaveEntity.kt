package com.emc.moodmingle.data.model.save

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "save")
data class SaveEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userUid: String,
    val postId: Int,
    val time: Long
)