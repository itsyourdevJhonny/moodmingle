package com.emc.moodmingle.data.model.post

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "reactions")
data class ReactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val postId: Int,
    val reactorId: String,
    @TypeConverters(ReactionTypeConverter::class)
    val reactionType: ReactionType
)