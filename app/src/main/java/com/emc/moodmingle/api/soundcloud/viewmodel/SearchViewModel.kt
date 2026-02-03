package com.emc.moodmingle.api.soundcloud.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.api.soundcloud.SoundCloudApi
import com.emc.moodmingle.api.soundcloud.model.TrackResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val soundCloudApi: SoundCloudApi) : ViewModel() {

    private val _tracks = MutableStateFlow<List<TrackResponse>>(emptyList())
    val tracks: StateFlow<List<TrackResponse>> = _tracks

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow(false)
    val error: StateFlow<Boolean> = _error

    val client by lazy { OkHttpClient() }

    fun searchTracks(query: String) {
        if (query.isBlank()) return
        _loading.value = true

        viewModelScope.launch {
            try {
                _tracks.value = soundCloudApi.searchTracks(query)
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = true
            } finally {
                _loading.value = false
            }
        }
    }

    suspend fun getPlayableUrlFromServer(trackId: Long): String? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("https://soundcloudbackend.onrender.com/api/track/playable?trackId=$trackId")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext null
                    val json = response.body?.string() ?: return@withContext null
                    JSONObject(json).optString("playableUrl", "")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
