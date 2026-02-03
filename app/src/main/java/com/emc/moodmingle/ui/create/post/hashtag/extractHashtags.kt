package com.emc.moodmingle.ui.create.post.hashtag

fun extractHashtags(text: String): List<String> {
    return text.lines()
        .map { it.removePrefix("#").trim() }
        .filter { it.isNotEmpty() }
}