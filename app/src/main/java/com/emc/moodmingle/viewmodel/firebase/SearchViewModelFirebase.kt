package com.emc.moodmingle.viewmodel.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.data.firebase.model.search.SearchEntityFirebase
import com.emc.moodmingle.data.firebase.repository.search.SearchRepositoryFirebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModelFirebase @Inject constructor(
    private val repository: SearchRepositoryFirebase
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<SearchEntityFirebase>>(emptyList())
    val searchResults: StateFlow<List<SearchEntityFirebase>> = _searchResults.asStateFlow()

    private val _userSearches = MutableStateFlow<List<SearchEntityFirebase>>(emptyList())
    val userSearches: StateFlow<List<SearchEntityFirebase>> = _userSearches.asStateFlow()

    fun setSearchResults(list: List<SearchEntityFirebase>) {
        _searchResults.value = list
    }

    fun searchUsers(usernameQuery: String) {
        viewModelScope.launch {
            repository.searchByUsername(usernameQuery)
                .catch { e -> println("Search error: ${e.message}") }
                .collect { results -> _searchResults.value = results }
        }
    }

    fun getUserSearches(userUid: String) {
        viewModelScope.launch {
            repository.getSearchesBySearcherId(userUid)
                .catch { e -> println("Get searches error: ${e.message}") }
                .collect { results -> _userSearches.value = results }
        }
    }

    fun getSearchesBySearcherId(searcherId: String) = repository.getSearchesBySearcherId(searcherId)

    fun addSearch(searchEntity: SearchEntityFirebase) {
        viewModelScope.launch {
            repository.insert(searchEntity)
        }
    }

    suspend fun getSearchBySearcherIdAndUserId(searcherId: String, userUid: String) = repository.getSearchBySearcherIdAndUserId(searcherId, userUid)

    fun deleteSearch(searchEntity: SearchEntityFirebase) {
        viewModelScope.launch {
            repository.delete(searchEntity)
        }
    }
}
