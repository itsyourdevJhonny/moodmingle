package com.emc.moodmingle.data.model

data class SpotifySearchResponse(val tracks: Tracks)

data class Tracks(val items: List<Track>)

data class Track(
    val name: String,
    val uri: String,
    val artists: List<Artist>,
    val album: Album
)

data class Artist(val name: String)

data class Album(val images: List<Image>)

data class Image(val url: String)
