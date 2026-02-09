package com.emc.moodmingle.api.giphy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GiphyViewModel @Inject constructor(
    private val repository: GiphyRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<GifObject>>(emptyList())
    val items: StateFlow<List<GifObject>> = _items

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow(false)
    val error: StateFlow<Boolean> = _error

    private var offset = 0
    private val limit = 25
    private var canLoadMore = true

    private val loadedIds = HashSet<String>()

    private var currentQuery: String? = null

    init {
        loadTrending() // INITIAL TRENDING PAGE 0
    }

    /** LOAD TRENDING GIFS + STICKERS FROM START */
    fun loadTrending() {
        currentQuery = null
        resetPaging()
        loadNextPage()
    }

    /** SEARCH GIFS + STICKERS WITH CLIENT-SIDE STRICT FILTER */
    fun search(query: String) {
        currentQuery = query.takeIf { it.isNotBlank() }
        resetPaging()
        loadNextPage()
    }

    /** LOAD NEXT PAGE FOR TRENDING OR SEARCH */
    fun loadNextPage() {
        if (_isLoading.value || !canLoadMore) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = false

            try {
                // FETCH FROM REPOSITORY
                val response = if (currentQuery != null) {
                    repository.searchGifsAndStickers(
                        query = currentQuery!!,
                        limit = limit,
                        offset = offset
                    )
                } else {
                    repository.trendingGifsAndStickers(
                        limit = limit,
                        offset = offset
                    )
                }

                // CLIENT-SIDE STRICT FILTER (ONLY TITLES MATCH QUERY)
                val filteredByQuery = currentQuery?.let { query ->
                    response.data.filter { gif ->
                        (gif.title ?: "").contains(query, ignoreCase = true)
                    }
                } ?: response.data

                // MP4-ONLY + DEDUPE + QUALITY PRIORITY
                val newItems = filteredByQuery
                    .asSequence()
                    .filter { gif ->
                        gif.images.original?.mp4 != null ||
                                gif.images.fixedWidth?.mp4 != null ||
                                gif.images.fixedHeight?.mp4 != null
                    }
                    .filter { loadedIds.add(it.id) }
                    .sortedByDescending { gif ->
                        when {
                            gif.images.original?.mp4 != null -> 3
                            gif.images.fixedWidth?.mp4 != null -> 2
                            gif.images.fixedHeight?.mp4 != null -> 1
                            else -> 0
                        }
                    }
                    .toList()

                _items.value = _items.value + newItems
                offset += limit
                canLoadMore = newItems.isNotEmpty()
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** PREFETCH NEXT PAGE WHEN USER SCROLLS NEAR END */
    fun prefetchIfNeeded(visibleIndex: Int, totalCount: Int) {
        if (visibleIndex >= totalCount - 6) {
            loadNextPage()
        }
    }

    /** RESET PAGING VARIABLES AND LOADED ITEMS */
    private fun resetPaging() {
        offset = 0
        canLoadMore = true
        _items.value = emptyList()
        loadedIds.clear()
    }
}