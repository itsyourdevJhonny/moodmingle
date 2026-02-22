package com.emc.moodmingle.domain.local.model.post

import androidx.room.TypeConverter

class ReactionTypeConverter {
    @TypeConverter
    fun toReactionType(value: String): ReactionType {
        return ReactionType.valueOf(value)
    }

    @TypeConverter
    fun fromReactionType(value: ReactionType): String {
        return value.name
    }
}