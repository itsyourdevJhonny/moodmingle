package com.emc.moodmingle.domain.remote.model.favorites

data class FavoritesCollectionEntity(
    val id: String = "",
    val userUid: String = "",
    val favoritesIds: List<String> = emptyList(),
    val name: String = "",
    val time: Long = 0L
)