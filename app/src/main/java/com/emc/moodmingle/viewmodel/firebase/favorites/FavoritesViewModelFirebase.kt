package com.emc.moodmingle.viewmodel.firebase.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.data.firebase.model.favorites.FavoritesEntityFirebase
import com.emc.moodmingle.data.firebase.repository.favorites.FavoritesRepositoryFirebase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModelFirebase @Inject constructor(
    private val repository: FavoritesRepositoryFirebase
) : ViewModel() {

    private val _userFavorites = MutableStateFlow<List<FavoritesEntityFirebase>>(emptyList())
    val userFavorites: StateFlow<List<FavoritesEntityFirebase>> = _userFavorites

    fun loadFavoritesByUser(userUid: String) {
        viewModelScope.launch {
            val favorites = repository.getFavoritesByUserUid(userUid)
            _userFavorites.value = favorites
        }
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val favoritesCollection = firestore.collection("favorites")

    fun getFavoritesByUser(userUid: String): Flow<List<FavoritesEntityFirebase>> = callbackFlow {
        val listener = favoritesCollection
            .whereEqualTo("userUid", userUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val favorites = snapshot?.documents?.mapNotNull {
                    it.toObject(FavoritesEntityFirebase::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(favorites)
            }

        awaitClose { listener.remove() }
    }

    fun getFavoriteByPostAndUser(postId: String, userUid: String, callback: (FavoritesEntityFirebase?) -> Unit) {
        viewModelScope.launch {
            val favorite = repository.getFavoritesByPostIdAndUserUid(postId, userUid)
            callback(favorite)
        }
    }

    fun insert(favoritesEntity: FavoritesEntityFirebase) {
        viewModelScope.launch {
            repository.insert(favoritesEntity)
        }
    }

    fun update(favoritesEntity: FavoritesEntityFirebase) {
        viewModelScope.launch {
            repository.update(favoritesEntity)
        }
    }

    fun delete(favoritesEntity: FavoritesEntityFirebase) {
        viewModelScope.launch {
            repository.delete(favoritesEntity)
        }
    }
}