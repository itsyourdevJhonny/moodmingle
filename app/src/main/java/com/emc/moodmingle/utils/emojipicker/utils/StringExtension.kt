package com.emc.moodmingle.utils.emojipicker.utils

internal fun String.capitalizeWords(): String {
    return this
        .split(' ')
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercaseChar() }
        }
}