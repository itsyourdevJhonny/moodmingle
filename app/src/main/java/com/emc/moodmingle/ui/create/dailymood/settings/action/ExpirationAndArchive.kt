package com.emc.moodmingle.ui.create.dailymood.settings.action

import com.emc.moodmingle.R

val ExpirationAndArchive = ActionGroup(
    groupName = "Expiration & Archive",
    groudIcon = R.drawable.time,
    actions = listOf(
        Action(
            icon = R.drawable.timer_filled,
            title = "Mood Duration",
            description = "Set how long your daily moods remain visible."
        ),
        Action(
            icon = R.drawable.archive_filled,
            title = "Auto Archive",
            description = "Automatically save expired moods to your private archive."
        )
    )
)