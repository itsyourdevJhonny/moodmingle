package com.emc.moodmingle.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.api.nominatim.LocationProvider
import com.emc.moodmingle.api.nominatim.NominatimApi
import com.emc.moodmingle.api.nominatim.NominatimPlace
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val api: NominatimApi,
    private val locationProvider: LocationProvider
) : ViewModel() {

    var query by mutableStateOf("")
        private set

    var searchResults by mutableStateOf<List<NominatimPlace>>(emptyList())
        private set

    var nearbyLocation by mutableStateOf<NominatimPlace?>(null)
        private set

    private var searchJob: Job? = null

    private var nearbyLoaded = false


    fun onQueryChange(value: String) {
        query = value

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            if (value.isNotBlank()) {
                searchResults = api.search(value)
            }
        }
    }

    fun loadNearby() {
        if (nearbyLoaded) return
        nearbyLoaded = true

        viewModelScope.launch {
            delay(1100)
            val location = locationProvider.getLocation() ?: return@launch
            nearbyLocation = api.reverse(location.latitude, location.longitude)
        }
    }
}
