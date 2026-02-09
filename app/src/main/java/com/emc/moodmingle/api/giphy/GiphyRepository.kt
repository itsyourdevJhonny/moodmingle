package com.emc.moodmingle.api.giphy

import javax.inject.Inject

class GiphyRepository @Inject constructor(
    private val api: GiphyApi
) {

    private val apiKey = "sFw8UHTDN0sXAmFrWwA6PSrJySkdfeu9"
    private val limit = 25

    // -------------------------
    // TRENDING
    // -------------------------
    suspend fun trendingGifsAndStickers(limit: Int = this.limit, offset: Int = 0): GiphyResponse {
        val gifs = api.trendingGifs(apiKey, limit, offset).data
        val stickers = api.trendingStickers(apiKey, limit, offset).data
        return GiphyResponse(data = gifs + stickers)
    }

    // -------------------------
    // SEARCH
    // -------------------------
    suspend fun searchGifsAndStickers(query: String, limit: Int = this.limit, offset: Int = 0): GiphyResponse {
        val gifs = api.searchGifs(apiKey, query, limit, offset).data
        val stickers = api.searchStickers(apiKey, query, limit, offset).data
        return GiphyResponse(data = gifs + stickers)
    }
}