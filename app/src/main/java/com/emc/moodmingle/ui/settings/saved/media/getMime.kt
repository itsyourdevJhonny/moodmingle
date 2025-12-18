package com.emc.moodmingle.ui.settings.saved.media

fun getMime(url: String): String {
    val extension = url.substringAfterLast('.', "").lowercase()
    val mime = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: ""
    return mime
}