package com.emc.moodmingle.ui.settings.favorites

fun filterUrlsByMimeType(urls: List<String>, mimeTypePrefix: String): List<String> {
    return urls.filter { getMimeTypeFromUrl(it)?.startsWith(mimeTypePrefix) == true }
}
fun getMimeTypeFromUrl(url: String): String? {
    val pattern = Regex(
        "https://res.cloudinary.com/[^/]+/(image|video|raw)/upload/[^/]+\\.(\\w+)",
        RegexOption.COMMENTS
    )

    val match = pattern.matchEntire(url)

    if (match != null) {
        val resourceType = match.groupValues[1]
        val format = match.groupValues[2]
        return  when(resourceType) {
            "image" -> "image/$format"
            "video" -> "video/$format"
            "raw" -> "audio/$format"
            else -> null
        }
    }
    return null
}