package com.emc.moodmingle.utils.text

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

fun String.toColor(): Color {
    return Color(this.toColorInt())
}