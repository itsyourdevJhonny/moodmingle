package com.emc.moodmingle.ui.create.dailymood.settings.util

// map SettingsScreenType to human-readable titles
private val screenTitleMap = mapOf(
    SettingsScreenType.ViewListVisibility to "View List Visibility",
    SettingsScreenType.ScreenshotAlerts to "Screenshot Alerts",
    SettingsScreenType.ReplayLimit to "Replay Limit",
    SettingsScreenType.HideMood to "Hide Mood from People",
    SettingsScreenType.NotifyPeople to "Notify People",
    SettingsScreenType.Timing to "Timing",
    SettingsScreenType.ReplyPermissions to "Reply Permissions",
    SettingsScreenType.ReactionSettings to "Reaction Settings",
    SettingsScreenType.SharingForwarding to "Sharing & Forwarding",
    SettingsScreenType.BlockedPeople to "Blocked People",
    SettingsScreenType.RestrictedAccounts to "Restricted Accounts",
    SettingsScreenType.MoodDuration to "Mood Duration",
    SettingsScreenType.AutoArchive to "Auto Archive",
    SettingsScreenType.SharePlatform to "Share Platform",
    SettingsScreenType.AllowDownloads to "Allow Downloads",
    SettingsScreenType.AutoSaveDevice to "Auto-Save to Device",
    SettingsScreenType.UploadQuality to "Upload Quality",
    SettingsScreenType.GhostMode to "Ghost Mode",
    SettingsScreenType.ScreenProtection to "Screen Recording Protection"
)

/**
 * Converts a SettingsScreenType into a human-readable title.
 */
fun SettingsScreenType.toTitle(): String = screenTitleMap[this] ?: "Unknown"

/**
 * Converts a human-readable title string back into a SettingsScreenType.
 * Returns null if no match is found.
 */
fun String.toSettingsScreenType(): SettingsScreenType? =
    screenTitleMap.entries.firstOrNull { it.value == this }?.key