package com.emc.moodmingle.utils.text

import androidx.compose.ui.text.style.TextAlign

fun String.toTextAlign(): TextAlign {
    return when(this) {
        "Unspecified" -> TextAlign.Unspecified
        "Center" -> TextAlign.Center
        "Start" -> TextAlign.Start
        "End" -> TextAlign.End
        "Left" -> TextAlign.Left
        "Right" -> TextAlign.Right
        else -> TextAlign.Justify
    }
}