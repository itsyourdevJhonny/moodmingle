package com.emc.moodmingle.utils.color

import androidx.compose.ui.graphics.Color

/**
 * Converts a compose Color to a hex string (#RRGGBB).
 */
fun Color.toHex(): String {
    val r = (red * 255).toInt().coerceIn(0, 255)
    val g = (green * 255).toInt().coerceIn(0, 255)
    val b = (blue * 255).toInt().coerceIn(0, 255)

    return String.format("#%02X%02X%02X", r, g, b)
}