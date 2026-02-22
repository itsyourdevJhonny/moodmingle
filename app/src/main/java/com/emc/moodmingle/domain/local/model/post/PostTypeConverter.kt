package com.emc.moodmingle.domain.local.model.post

import androidx.room.TypeConverter

class PostTypeConverter {
    @TypeConverter
    fun toPostType(value: String): PostType {
        return PostType.valueOf(value)
    }

    @TypeConverter
    fun fromPostType(value: PostType): String {
        return value.name
    }
}