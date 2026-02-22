package com.emc.moodmingle.ui.settings.saved.collection

import com.emc.moodmingle.domain.remote.model.saved.CollectionEntityFirebase
import com.emc.moodmingle.domain.remote.model.saved.SaveEntityFirebase
import com.emc.moodmingle.viewmodel.remote.saved.CollectionViewModelFirebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

fun displayCollectionItems(
    userId: String,
    collection: CollectionEntityFirebase,
    saved: List<SaveEntityFirebase>,
    collectionViewModelFirebase: CollectionViewModelFirebase,
    scope: CoroutineScope,
    onSelectedType: (String) -> Unit,
    onGroupBy: (List<SaveEntityFirebase>) -> Unit,
    onSelectedCollectionName: (String) -> Unit
) {
    scope.launch {
        val userCollections = collectionViewModelFirebase
            .getCollectionByUser(userId)
            .first()
        val selectedCollection =
            userCollections.find { it.name == collection.name }

        val groupedSaves: List<SaveEntityFirebase> =
            if (selectedCollection != null && collection.name != "None") {
                saved.filter { savedItem ->
                    selectedCollection.saveIds.contains(savedItem.id)
                }
            } else {
                saved
            }

        onSelectedType("All")
        onSelectedCollectionName(collection.name)
        onGroupBy(groupedSaves)
    }
}