package com.emc.moodmingle.ui.create.dailymood.settings.action

import com.emc.moodmingle.R

val DownloadAndDataControls = ActionGroup(
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
)