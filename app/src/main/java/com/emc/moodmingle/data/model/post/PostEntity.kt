package com.emc.moodmingle.data.model.post

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val username: String,
    val avatarUrl: String,
    val mood: String,
    val moodEmoji: String,
    val hashtag: String,
    val caption: String,
    val description: String,
    val timeAgo: Long,
    val comments: Long = 0,
    val likes: Long = 0,
    val shares: Long = 0,
    @TypeConverters(PostTypeConverter::class)
    val type: PostType,
    val url: String = ""
)

