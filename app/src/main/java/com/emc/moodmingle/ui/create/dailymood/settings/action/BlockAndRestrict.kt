package com.emc.moodmingle.ui.create.dailymood.settings.action

import com.emc.moodmingle.R

val BlockAndRestrict = ActionGroup(
    groupName = "Block & Restrict",
    groudIcon = R.drawable.block_user,
    actions = listOf(
        Action(
            icon = R.drawable.block_filled,
            title = "Blocked People",
            description = "Manage people who are blocked from viewing or interacting with your moods."
        ),
        Action(
            icon = R.drawable.restrict_filled,
            title = "Restricted Accounts",
            description = "Manage accounts that are restricted from viewing or interacting with your moods."
        )
    )
)