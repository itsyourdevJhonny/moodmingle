package com.emc.moodmingle.domain.local.model.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val uid: String,
    val username: String,
    val email: String,
    val password: String,
    val avatarUrl: String,
    val bio: String = "",
    val joinedDate: String = getCurrentDate()
)

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return dateFormat.format(Date())
}