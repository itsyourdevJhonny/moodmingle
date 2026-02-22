package com.emc.moodmingle.domain.local.model.search

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search")
data class SearchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userUid: String,
    val time: Long
)