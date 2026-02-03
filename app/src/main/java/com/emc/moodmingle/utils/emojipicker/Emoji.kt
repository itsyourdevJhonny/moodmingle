package com.emc.moodmingle.utils.emojipicker

import emoji.core.model.NetworkEmoji

data class Emoji(
    val character: String,
    val codePoint: String,
    val group: String,
    val subgroup: String,
    val unicodeName: String,
) {
    internal constructor(networkEmoji: NetworkEmoji) : this(
        character = networkEmoji.character,
        codePoint = networkEmoji.codePoint,
        group = networkEmoji.group,
        subgroup = networkEmoji.subgroup,
        unicodeName = networkEmoji.unicodeName,
    )
}