package com.emc.moodmingle.utils.pagination

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.filterNotNull

suspend fun executePagination(
    listState: LazyListState,
    visibleList: List<Any>,
    originalList: List<Any>,
    pageSize: Int,
    onLoadedCount: (Int) -> Unit
) {
    snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
        .filterNotNull()
        .collect { lastVisible ->
            val nearEnd = visibleList.lastIndex - 1
            if (lastVisible >= nearEnd && visibleList.size < originalList.size) {
                // load next batch
                onLoadedCount((visibleList.size + pageSize).coerceAtMost(originalList.size))
            }
        }
}