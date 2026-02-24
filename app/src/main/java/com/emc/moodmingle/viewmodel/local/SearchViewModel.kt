package com.emc.moodmingle.viewmodel.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.domain.local.model.user.UserEntity
import com.emc.moodmingle.domain.local.model.search.SearchEntity
import com.emc.moodmingle.domain.local.service.SearchService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val searchService: SearchService) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<UserEntity>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    fun setSearchResults(list: List<UserEntity>) {
        _searchResults.value = list
    }

    fun insertSearch(searchEntity: SearchEntity) = viewModelScope.launch {
        searchService.insertSearch(searchEntity)
    }

    fun getSearchHistory(userUid: String): Flow<List<SearchEntity>> {
        return searchService.getSearchHistory(userUid)
    }

    fun searchByUsername(username: String): Flow<List<SearchEntity>> {
        return searchService.searchByUsername(username)
    }

    fun updateSearch(searchEntity: SearchEntity) = viewModelScope.launch {
        searchService.updateSearch(searchEntity)
    }

    fun deleteSearch(searchEntity: SearchEntity) = viewModelScope.launch {
        searchService.deleteSearch(searchEntity)
    }
}