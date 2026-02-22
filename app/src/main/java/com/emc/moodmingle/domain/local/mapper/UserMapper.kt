package com.emc.moodmingle.domain.local.mapper

import com.emc.moodmingle.domain.local.model.user.UserEntity
import com.emc.moodmingle.domain.remote.auth.FirebaseUser

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