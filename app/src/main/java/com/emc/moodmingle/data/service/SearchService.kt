package com.emc.moodmingle.data.service

import com.emc.moodmingle.data.dao.search.SearchDao
import com.emc.moodmingle.data.model.search.SearchEntity
import javax.inject.Inject

class SearchService @Inject constructor(private val searchDao: SearchDao) {
    fun getSearchHistory(userUid: String) = searchDao.getSearchesByUserUid(userUid)
    fun searchByUsername(username: String) = searchDao.searchByUsername(username)
    suspend fun insertSearch(searchEntity: SearchEntity) = searchDao.insert(searchEntity)
    suspend fun updateSearch(searchEntity: SearchEntity) = searchDao.update(searchEntity)
    suspend fun deleteSearch(searchEntity: SearchEntity) = searchDao.delete(searchEntity)
}