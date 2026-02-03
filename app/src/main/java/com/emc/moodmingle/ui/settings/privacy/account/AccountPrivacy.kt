package com.emc.moodmingle.ui.settings.privacy.account

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase

@Composable
fun AccountPrivacy(userEntityFirebase: UserEntityFirebase) {
    Column {
        AccountVisibility(userEntityFirebase)
        MoodNoteVisibility(userEntityFirebase)
        AccountVerification(userEntityFirebase)
    }
}