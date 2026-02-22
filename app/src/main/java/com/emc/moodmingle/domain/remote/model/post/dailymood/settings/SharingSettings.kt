package com.emc.moodmingle.domain.remote.model.post.dailymood.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SharingSettings(
    val allowExternalSharing: Boolean = true,
    val allowForwarding: Boolean = true
) : Parcelable