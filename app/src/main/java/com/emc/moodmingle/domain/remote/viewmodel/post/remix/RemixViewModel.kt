package com.emc.moodmingle.domain.remote.viewmodel.post.remix

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.domain.remote.model.post.remix.RemixEntity
import com.emc.moodmingle.domain.remote.repository.remix.RemixRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemixViewModel @Inject constructor(
    private val remixRepository: RemixRepository
) : ViewModel() {

    /**
     * inserts a new remix into firestore
     */
    fun insertRemix(remixEntity: RemixEntity) {
        viewModelScope.launch {
            remixRepository.insert(remixEntity)
        }
    }

    fun getAllRemixes() = remixRepository.getAllRemixes()

    /**
     * returns a realtime flow of a remix by its id
     */
    fun getRemixById(id: String) = remixRepository.getRemixById(id)

    /**
     * returns all remixes created by a specific user
     */
    fun getRemixesByUserId(userId: String) = remixRepository.getRemixedByUserId(userId)

    /**
     * returns the remix count for a specific user
     */
    fun getRemixCountByUserId(userId: String) = remixRepository.getRemixCountByUserId(userId)

    /**
     * updates an existing remix document
     */
    fun updateRemix(remixEntity: RemixEntity) {
        viewModelScope.launch {
            remixRepository.update(remixEntity)
        }
    }

    /**
     * deletes an existing remix document
     */
    fun deleteRemix(remixEntity: RemixEntity) {
        viewModelScope.launch {
            remixRepository.delete(remixEntity)
        }
    }
}
