package com.emc.moodmingle.utils.text

object TextFormatter {
    fun formatTextWithSuffixS(text: String): String {
        return "$text${if (text.endsWith("s")) "'" else "'s"}"
    }
}