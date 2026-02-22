package com.emc.moodmingle.viewmodel.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.domain.local.model.favorites.FavoritesEntity
import com.emc.moodmingle.domain.local.service.FavoritesService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val favoritesService: FavoritesService) : ViewModel() {

    fun insert(favoritesEntity: FavoritesEntity) = viewModelScope.launch {
        favoritesService.insert(favoritesEntity)
    }

    fun getFavoritesByPostIdAndUserUid(postId: Int, userUid: String) = flow {
        emit(favoritesService.getFavoritesByPostIdAndUserUid(postId, userUid))
    }.flowOn(Dispatchers.IO)

    fun getFavoritesByUserUid(userUid: String) = flow {
        emit(favoritesService.getFavoritesByUserUid(userUid))
    }.flowOn(Dispatchers.IO)

    fun update(favoritesEntity: FavoritesEntity) = viewModelScope.launch {
        favoritesService.update(favoritesEntity)
    }

    fun delete(favoritesEntity: FavoritesEntity) = viewModelScope.launch {
        favoritesService.delete(favoritesEntity)
    }
}