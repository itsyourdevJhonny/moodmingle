package com.emc.moodmingle.domain.remote.auth

data class FirebaseUser(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val avatarUrl: String = "",
    val bio: String = "",
    val joinedDate: String = ""
)
