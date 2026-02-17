package com.emc.moodmingle.ui.dailymood.settings.privacy

import com.emc.moodmingle.R

fun getDailyMoodSettingsActions(): List<ActionGroup> {
    return listOf(
        ActionGroup(
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
        ),

        ActionGroup(
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
        ),

        ActionGroup(
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
        ),

        ActionGroup(
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
                ),
                Action(
                    icon = R.drawable.delete,
                    title = "Expire Mood Instantly",
                    description = "Remove your active mood immediately."
                )
            )
        ),

        ActionGroup(
            groupName = "Download & Data Controls",
            groudIcon = R.drawable.data_download,
            actions = listOf(
                Action(
                    icon = R.drawable.share_platform,
                    title = "Share Platform",
                    description = "Select the platform you want to share your mood with."
                ),
                Action(
                    icon = R.drawable.download,
                    title = "Allow Downloads",
                    description = "Choose whether viewers can download your mood."
                ),
                Action(
                    icon = R.drawable.save_post,
                    title = "Auto-Save to Device",
                    description = "Automatically save posted moods to your device."
                ),
                Action(
                    icon = R.drawable.quality_filled,
                    title = "Upload Quality",
                    description = "Select high quality or data-saving upload mode."
                )
            )
        ),

        ActionGroup(
            groupName = "Stealth & Privacy Modes",
            groudIcon = R.drawable.privacy,
            actions = listOf(
                Action(
                    icon = R.drawable.ghost_filled,
                    title = "Ghost Mode",
                    description = "Hide your online status while viewing moods."
                ),
                Action(
                    icon = R.drawable.private_filled,
                    title = "Private Mood Mode",
                    description = "Share moods with selected people only and restrict forwarding."
                ),
                Action(
                    icon = R.drawable.record_filled,
                    title = "Screen Recording Protection",
                    description = "Prevent screen recording while viewing your mood."
                )
            )
        )
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