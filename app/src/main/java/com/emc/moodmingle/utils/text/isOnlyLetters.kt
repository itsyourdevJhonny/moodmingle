package com.emc.moodmingle.utils.text

fun String.isOnlyLetters(): Boolean {
    return this.matches(Regex("^[a-zA-Z]+$"))
}
