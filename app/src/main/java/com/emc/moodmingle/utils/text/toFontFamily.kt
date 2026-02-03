package com.emc.moodmingle.utils.text

import androidx.compose.ui.text.font.FontFamily
import com.emc.moodmingle.utils.font.FontUtils

fun String.toFontFamily(): FontFamily {
    return FontUtils.getDefaultFonts()
        .filter { fontOption -> fontOption.name == this }
        .map { it.fontFamily }[0]
}