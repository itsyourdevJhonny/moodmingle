package com.emc.moodmingle.ui.settings.encryption

import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase

fun getInformationTypes(userEntity: UserEntityFirebase?):  List<Triple<List<Triple<String, String, Map<String, Boolean>>>, String, Int>>? {
    return userEntity?.let { user ->
        listOf(
            Triple(
                listOf(
                    Triple("Email", user.email, user.emailEncrypt),
                    Triple("Username", user.username, user.usernameEncrypt),
                    Triple("Password", user.password, user.passwordEncrypt),
                    Triple("Bio", user.bio, user.bioEncrypt),
                    Triple("Avatar", user.avatarUrl, user.avatarEncrypt)
                ),
                "Personal Information",
                R.drawable.description
            ),
            Triple(
                listOf(
                    Triple("Hashtag", "Hashtag", user.hashtagEncrypt),
                    Triple("Caption", "Caption", user.captionEncrypt),
                    Triple("Description", "Description", user.descriptionEncrypt),
                    Triple("Mood Text", "Mood Text", user.moodTextEncrypt),
                    Triple("Mood Emoji", "Mood Emoji", user.moodEmojiEncrypt)
                ),
                "Post",
                R.drawable.post
            )
        )
    }
}