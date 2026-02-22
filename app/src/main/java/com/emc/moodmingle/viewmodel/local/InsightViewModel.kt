package com.emc.moodmingle.viewmodel.local

import com.emc.moodmingle.domain.remote.repository.insight.InsightRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.ui.screens.InsightData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for loading and exposing insight data to the UI.
 */
@HiltViewModel
class InsightViewModel @Inject constructor(
    private val repository: InsightRepository
) : ViewModel() {
    private val _insightData = MutableStateFlow(InsightData(0, 0, 0, 0.0))
    private val _previousInsightData = MutableStateFlow(InsightData(0, 0, 0, 0.0))

    val insightData: StateFlow<InsightData> = _insightData
    val previousInsightData: StateFlow<InsightData> = _previousInsightData

    /**
     * Loads insights for the specified user.
     */
    fun loadInsights(userId: String, selectedPeriod: String) {
        viewModelScope.launch {
            repository.getUserInsights(userId, selectedPeriod).collectLatest {
                _insightData.value = it
            }
        }
    }

    fun loadPreviousInsights(userId: String, selectedPeriod: String) {
        viewModelScope.launch {
            repository.getPreviousUserInsights(userId, selectedPeriod).collectLatest {
                _insightData.value = it
            }
        }
    }
}
