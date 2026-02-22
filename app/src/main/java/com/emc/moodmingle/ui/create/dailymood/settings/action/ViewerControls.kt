package com.emc.moodmingle.ui.create.dailymood.settings.action

import com.emc.moodmingle.R

val ViewerControls = ActionGroup(
    groupName = "Viewer Controls",
    groudIcon = R.drawable.viewer,
    actions = listOf(
        Action(
            icon = R.drawable.view,
            title = "View List Visibility",
            description = "Choose whether you can see who viewed your daily moods."
        ),
        Action(
            icon = R.drawable.screenshot,
            title = "Screenshot Alerts",
            description = "Receive a notification when someone screenshots your mood."
        ),
        Action(
            icon = R.drawable.replay_filled,
            title = "Replay Limit",
            description = "Control how many times people can replay your mood."
        ),
        Action(
            icon = R.drawable.hidden,
            title = "Hide Mood from Specific People",
            description = "Select people who won’t be able to see your daily moods."
        ),
        Action(
            icon = R.drawable.notify,
            title = "Notify People",
            description = "Set up notifications for new moods from selected people."
        ),
        Action(
            icon = R.drawable.timing_filled,
            title = "Timing",
            description = "Schedule your moods to post at specific times and dates."
        )
    )
)