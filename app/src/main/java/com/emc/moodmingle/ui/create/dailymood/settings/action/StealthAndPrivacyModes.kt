package com.emc.moodmingle.ui.create.dailymood.settings.action

import com.emc.moodmingle.R

val StealthAndPrivacyModes = ActionGroup(
    groupName = "Stealth & Protection",
    groudIcon = R.drawable.privacy,
    actions = listOf(
        Action(
            icon = R.drawable.ghost_filled,
            title = "Ghost Mode",
            description = "Hide your online status while viewing moods."
        ),
        Action(
            icon = R.drawable.record_filled,
            title = "Screen Recording Protection",
            description = "Prevent screen recording while viewing your mood."
        )
    )
)