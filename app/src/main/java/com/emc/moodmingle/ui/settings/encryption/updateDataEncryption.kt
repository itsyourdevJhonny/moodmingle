package com.emc.moodmingle.ui.settings.encryption

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.snapshotFlow
import com.emc.moodmingle.data.firebase.model.UserEntityFirebase
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

fun updateDataEncryption(
    scope: CoroutineScope,
    userViewModelFirebase: FirebaseUserViewModel,
    label: String,
    userEntity: UserEntityFirebase,
    newEncryptedValue: String,
    isEncrypted: Boolean,
    title: String,
    state: ScrollState
) {
    scope.launch {
        userViewModelFirebase.updateUser(
            userEntity = when (label) {
                "Email" -> userEntity.copy(emailEncrypt = mapOf(newEncryptedValue to !isEncrypted))
                "Username" -> userEntity.copy(usernameEncrypt = mapOf(newEncryptedValue to !isEncrypted))
                "Password" -> userEntity.copy(passwordEncrypt = mapOf(newEncryptedValue to !isEncrypted))
                "Bio" -> userEntity.copy(bioEncrypt = mapOf(newEncryptedValue to !isEncrypted))
                "Avatar" -> userEntity.copy(avatarEncrypt = mapOf(newEncryptedValue to !isEncrypted))
                "Hashtag" -> userEntity.copy(hashtagEncrypt = mapOf(newEncryptedValue to !isEncrypted))
                "Caption" -> userEntity.copy(captionEncrypt = mapOf(newEncryptedValue to !isEncrypted))
                "Description" -> userEntity.copy(descriptionEncrypt = mapOf(newEncryptedValue to !isEncrypted))
                "Mood Text" -> userEntity.copy(moodTextEncrypt = mapOf(newEncryptedValue to !isEncrypted))
                "Mood Emoji" -> userEntity.copy(moodEmojiEncrypt = mapOf(newEncryptedValue to !isEncrypted))
                else -> userEntity.copy(emailEncrypt = mapOf(newEncryptedValue to !isEncrypted))
            }
        )

        if (!isEncrypted && title == "Post") {
            snapshotFlow { state.maxValue }.firstOrNull()
            state.animateScrollTo(state.maxValue)
        }
    }
}