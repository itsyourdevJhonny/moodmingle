package com.emc.moodmingle.ui.dailymood.settings.action

fun getDailyMoodSettingsActions(): List<ActionGroup> {
    return listOf(
        ViewerControls,
        InteractionControls,
        BlockAndRestrict,
        ExpirationAndArchive,
        DownloadAndDataControls,
        StealthAndPrivacyModes
    )
}

data class ActionGroup(
    val groupName: String,
    val groudIcon: Int,
    val actions: List<Action>,
)

data class Action(
    val icon: Int,
    val title: String,
    val description: String,
)