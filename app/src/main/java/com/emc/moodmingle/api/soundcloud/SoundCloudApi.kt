package com.emc.moodmingle.api.soundcloud

import com.emc.moodmingle.api.soundcloud.model.TrackResponse
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface SoundCloudApi {
    @GET("/api/search")
//    @Headers("Cache-Control: max-age=60")
    suspend fun searchTracks(@Query("q") query: String): List<TrackResponse>

    @GET("/api/track/playable")
    @Headers("Cache-Control: max-age=60")
    suspend fun searchStreamUrl(@Query("trackId") trackId: Long): ResponseBody
}