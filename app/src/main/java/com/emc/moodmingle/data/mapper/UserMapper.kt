package com.emc.moodmingle.data.mapper

import com.emc.moodmingle.data.model.UserEntity
import com.emc.moodmingle.data.remote.FirebaseUser

object UserMapper {
    fun mapToLocal(firebaseUser: FirebaseUser): UserEntity {
        return UserEntity(
            uid = firebaseUser.uid,
            username = firebaseUser.username,
            email = firebaseUser.email,
            password = firebaseUser.password,
            avatarUrl = firebaseUser.avatarUrl,
        )
    }
}