package com.emc.moodmingle.data.model.favorites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoritesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userUid: String,
    val postId: Int,
    val time: Long
)