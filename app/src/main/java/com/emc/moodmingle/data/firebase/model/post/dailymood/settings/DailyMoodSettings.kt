package com.emc.moodmingle.data.firebase.model.post.dailymood.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DailyMoodSettings(
    val viewListEnabled: Boolean = true,
    val screenshotAlertEnabled: Boolean = false,
    val replay: ReplayLimit = ReplayLimit(),
    val hiddenUserIds: List<String> = emptyList(),
    val timing: SettingsTiming = SettingsTiming(),
    val sharePlatformType: SharePlatformType = SharePlatformType.NONE,
    val notifyType: NotifyType = NotifyType.NONE,
    val replyPermissionType: ReplyPermissionType = ReplyPermissionType.EVERYONE,
    val reactionEnabled: Boolean = true,
    val sharingSettings: SharingSettings = SharingSettings(),
    val blockedUserIds: Set<String> = emptySet(),
    val restrictedUserIds: Set<String> = emptySet(),
    val duration: MoodDuration = MoodDuration(),
    val autoArchive: Boolean = true,
    val allowDownloads: Boolean = true,
    val autoSaveToDevice: Boolean = false,
    val uploadQuality: UploadQuality = UploadQuality.HIGH,
    val ghostMode: Boolean = false,
    val screenProtectionEnabled: Boolean = true
) : Parcelable