package com.emc.moodmingle.utils.text

import androidx.compose.ui.text.font.FontFamily
import com.emc.moodmingle.utils.font.FontUtils

fun FontFamily.toFontName(): String {
    return FontUtils.getDefaultFonts()
        .filter { (_, font) -> font == this }
        .map { it.name }[0]
}