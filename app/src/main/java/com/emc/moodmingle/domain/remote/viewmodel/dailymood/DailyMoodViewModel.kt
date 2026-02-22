package com.emc.moodmingle.domain.remote.viewmodel.dailymood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.domain.remote.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.domain.remote.repository.dailymood.DailyMoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyMoodViewModel @Inject constructor(
    private val repository: DailyMoodRepository,
) : ViewModel() {

    private val _dailyMoods = MutableStateFlow<List<DailyMoodEntity>>(emptyList())
    val dailyMoods: StateFlow<List<DailyMoodEntity>> = _dailyMoods.asStateFlow()

    private val _activeDailyMoods = MutableStateFlow<List<DailyMoodEntity>>(emptyList())
    val activeDailyMoods = _activeDailyMoods.asStateFlow()

    private val _allActiveDailyMoods = MutableStateFlow<List<DailyMoodEntity>>(emptyList())

    val allActiveDailyMoods = _allActiveDailyMoods.asStateFlow()

    private val _operationState = MutableStateFlow<Result<Unit>?>(null)
    val operationState: StateFlow<Result<Unit>?> = _operationState.asStateFlow()

    /**
     * Observe all active daily moods in the system.
     */
    fun observeAllActiveDailyMoods() {
        viewModelScope.launch {
            repository.getAllActiveDailyMoods()
                .collect {
                    _allActiveDailyMoods.value = it
                }
        }
    }

    /**
     * Observe only active and non-expired moods.
     */
    fun observeActiveDailyMoods(userId: String) {
        viewModelScope.launch {
            repository.getActiveDailyMoodsByUserId(userId)
                .collect {
                    _activeDailyMoods.value = it
                }
        }
    }

    fun getDailyMoodsByUserId(userId: String) = repository.getDailyMoodsByUserId(userId)

    /**
     * Observe daily moods of a specific user.
     */
    fun observeDailyMoods(userId: String) {
        viewModelScope.launch {
            repository.getDailyMoodsByUserId(userId)
                .collect {
                    _dailyMoods.value = it
                }
        }
    }

    /**
     * Create new daily mood.
     */
    fun createDailyMood(mood: DailyMoodEntity) {
        viewModelScope.launch {
            repository.createDailyMood(mood)
        }
    }

    /**
     * Update daily mood.
     */
    fun updateDailyMood(mood: DailyMoodEntity) {
        viewModelScope.launch {
            _operationState.value = repository.updateDailyMood(mood)
        }
    }

    /**
     * Delete daily mood.
     */
    fun deleteDailyMood(id: String) {
        viewModelScope.launch {
            _operationState.value = repository.deleteDailyMood(id)
        }
    }

    /**
     * Get single mood once.
     */
    fun getDailyMoodById(id: String, onResult: (DailyMoodEntity?) -> Unit) {
        viewModelScope.launch {
            repository.getDailyMoodById(id)
                .onSuccess { onResult(it) }
                .onFailure { onResult(null) }
        }
    }
}