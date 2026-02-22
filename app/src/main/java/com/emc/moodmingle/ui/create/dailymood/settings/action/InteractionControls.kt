package com.emc.moodmingle.ui.create.dailymood.settings.action

import com.emc.moodmingle.R

val InteractionControls = ActionGroup(
    groupName = "Interaction Controls",
    groudIcon = R.drawable.interaction,
    actions = listOf(
        Action(
            icon = R.drawable.reply,
            title = "Reply Permissions",
            description = "Choose who can reply to your daily moods."
        ),
        Action(
            icon = R.drawable.love,
            title = "Reaction Settings",
            description = "Allow or disable reactions to your moods."
        ),
        Action(
            icon = R.drawable.share,
            title = "Sharing & Forwarding",
            description = "Control whether others can share or forward your mood."
        )
    )
)