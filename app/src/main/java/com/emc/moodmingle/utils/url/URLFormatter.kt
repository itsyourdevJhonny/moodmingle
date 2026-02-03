package com.emc.moodmingle.utils.url

object URLFormatter {
    enum class MediaType {
        IMAGE,
        VIDEO,
        AUDIO,
        FILE
    }

    fun getCloudinaryMediaType(url: String): MediaType {
        return when {
            "/image/" in url -> MediaType.IMAGE
            "/video/" in url -> MediaType.VIDEO
            url.endsWith(".mp3", true) || url.endsWith(".m4a", true) || url.endsWith(".wav", true) -> MediaType.AUDIO

            else -> MediaType.FILE
        }
    }

}