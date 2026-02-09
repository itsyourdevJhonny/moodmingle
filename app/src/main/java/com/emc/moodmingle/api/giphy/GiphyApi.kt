package com.emc.moodmingle.api.giphy

import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyApi {

    // -------------------------
    // TRENDING GIFS / STICKERS
    // -------------------------
    @GET("v1/gifs/trending")
    suspend fun trendingGifs(
        @Query("api_key") apiKey: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): GiphyResponse

    @GET("v1/stickers/trending")
    suspend fun trendingStickers(
        @Query("api_key") apiKey: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): GiphyResponse

    // -------------------------
    // SEARCH GIFS / STICKERS
    // -------------------------
    @GET("v1/gifs/search")
    suspend fun searchGifs(
        @Query("api_key") apiKey: String,
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("strict") strict: Boolean = true
    ): GiphyResponse

    @GET("v1/stickers/search")
    suspend fun searchStickers(
        @Query("api_key") apiKey: String,
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): GiphyResponse
}