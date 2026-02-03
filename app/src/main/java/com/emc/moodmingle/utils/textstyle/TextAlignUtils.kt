package com.emc.moodmingle.utils.textstyle

import androidx.compose.ui.text.style.TextAlign

object TextAlignUtils {
    fun getTextAlign(name: String): TextAlign {
        return when(name) {
            "Unspecified" -> TextAlign.Unspecified
            "Center" -> TextAlign.Center
            "Start" -> TextAlign.Start
            "End" -> TextAlign.End
            "Left" -> TextAlign.Left
            "Right" -> TextAlign.Right
            else -> TextAlign.Justify
        }
    }
}