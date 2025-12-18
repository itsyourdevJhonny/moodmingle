package com.emc.moodmingle.data.remote

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FirebaseUser(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val avatarUrl: String = "",
    val bio: String = "",
    val joinedDate: String = ""
)

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return dateFormat.format(Date())
}
