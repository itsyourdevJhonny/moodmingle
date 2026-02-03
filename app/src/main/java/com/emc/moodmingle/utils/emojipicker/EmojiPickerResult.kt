package com.emc.moodmingle.utils.emojipicker

internal sealed interface EmojiPickerResult<out T> {
    data object Loading : EmojiPickerResult<Nothing>

    data class Error(val exception: Throwable? = null) : EmojiPickerResult<Nothing>

    data class Success<T>(val data: T) : EmojiPickerResult<T>
}