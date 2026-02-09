package com.emc.moodmingle.api.giphy

import com.squareup.moshi.Json

data class GiphyResponse(
    val data: List<GifObject>
)

data class GifObject(
    val id: String = "",
    val title: String? = null,
    val images: GifImages
)

data class GifImages(
    val original: GifFormat?,
    @Json(name = "fixed_width") val fixedWidth: GifFormat?,
    @Json(name = "fixed_height") val fixedHeight: GifFormat?
)

data class GifFormat(
    val url: String = "",
    val mp4: String? = null,
    val webp: String? = null
)
