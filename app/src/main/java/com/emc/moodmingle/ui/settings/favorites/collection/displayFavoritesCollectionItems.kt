package com.emc.moodmingle.ui.settings.favorites.collection

import com.emc.moodmingle.domain.remote.model.favorites.FavoritesCollectionEntity
import com.emc.moodmingle.domain.remote.model.favorites.FavoritesEntityFirebase
import com.emc.moodmingle.viewmodel.remote.favorites.FavoritesCollectionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

fun displayFavoritesCollectionItems(
    userId: String,
    collection: FavoritesCollectionEntity,
    favorites: List<FavoritesEntityFirebase>,
    collectionViewModel: FavoritesCollectionViewModel,
    scope: CoroutineScope,
    onSelectedType: (String) -> Unit,
    onGroupByCollection: (List<FavoritesEntityFirebase>) -> Unit
) {
    scope.launch {
        val userCollections = collectionViewModel
            .getCollectionByUser(userId)
            .first()
        val selectedCollection =
            userCollections.find { it.name == collection.name }

        val groupedSaves: List<FavoritesEntityFirebase> =
            if (selectedCollection != null && collection.name != "None") {
                favorites.filter { savedItem ->
                    selectedCollection.favoritesIds.contains(savedItem.id)
                }
            } else {
                favorites
            }

        onSelectedType("Favorites")
        onGroupByCollection(groupedSaves)
    }
}