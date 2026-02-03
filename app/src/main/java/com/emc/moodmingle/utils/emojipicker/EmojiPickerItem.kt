package com.emc.moodmingle.utils.emojipicker

internal sealed class EmojiPickerItem {
    data class EmojiGroupHeader(val title: String) : EmojiPickerItem()

    data class EmojiGroupItems(val emojis: List<Emoji>) : EmojiPickerItem()
}