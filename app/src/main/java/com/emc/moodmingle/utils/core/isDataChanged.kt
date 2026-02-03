package com.emc.moodmingle.utils.core

fun isDataChanged(original: Any, copy: Any): Boolean {
    return original != copy
}