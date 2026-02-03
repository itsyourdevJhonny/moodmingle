package com.emc.moodmingle.utils.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

@Composable
fun dialogFullSizeProperties(): DialogProperties {
    return DialogProperties(
        usePlatformDefaultWidth = false,
        decorFitsSystemWindows = false
    )
}