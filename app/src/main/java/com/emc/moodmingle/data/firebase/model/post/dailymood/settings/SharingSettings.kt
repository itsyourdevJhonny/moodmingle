package com.emc.moodmingle.data.firebase.model.post.dailymood.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SharingSettings(
    val allowExternalSharing: Boolean = true,
    val allowForwarding: Boolean = true
) : Parcelable