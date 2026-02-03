package com.emc.moodmingle.utils.emojipicker.parser

import emoji.core.model.NetworkEmoji

internal interface EmojiParser {
    fun parseEmojiData(
        data: String,
        isSkinTonesSupported: Boolean = false,
    ): List<NetworkEmoji>
}