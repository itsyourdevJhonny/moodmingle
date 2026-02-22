package com.emc.moodmingle.domain.local.service

import com.emc.moodmingle.domain.local.dao.favorites.FavoritesDao
import com.emc.moodmingle.domain.local.model.favorites.FavoritesEntity
import javax.inject.Inject

class FavoritesService @Inject constructor(private val favoritesDao: FavoritesDao) {
    suspend fun insert(favoritesEntity: FavoritesEntity) = favoritesDao.insert(favoritesEntity)
    suspend fun getFavoritesByUserUid(userUid: String) = favoritesDao.getFavoritesByUserUid(userUid)
    suspend fun getFavoritesByPostIdAndUserUid(postID: Int, userUid: String) =
        favoritesDao.getFavoritesByPostIdAndUserUid(postID, userUid)

    suspend fun update(favoritesEntity: FavoritesEntity) = favoritesDao.update(favoritesEntity)
    suspend fun delete(favoritesEntity: FavoritesEntity) = favoritesDao.delete(favoritesEntity)
}