package com.emc.moodmingle.viewmodel.firebase.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.data.firebase.model.saved.SaveEntityFirebase
import com.emc.moodmingle.data.firebase.repository.saved.SaveRepositoryFirebase
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
class SaveViewModelFirebase @Inject constructor(
    private val repository: SaveRepositoryFirebase,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val collection = firestore.collection("save")

    private val _userSaves = MutableStateFlow<List<SaveEntityFirebase>>(emptyList())
    val userSaves: StateFlow<List<SaveEntityFirebase>> = _userSaves

    fun loadSavedByUser(userUid: String) {
        viewModelScope.launch {
            val saves = repository.getSavedByUserUid(userUid)
            _userSaves.value = saves
        }
    }

    fun deleteAll(saves: List<SaveEntityFirebase>) {
        viewModelScope.launch {
            if (saves.isEmpty()) return@launch

            val batch = firestore.batch()
            val collectionRef = firestore.collection("save")

            saves.forEach { save ->
                val docRef = collectionRef.document(save.id)
                batch.delete(docRef)
            }

            batch.commit()
                .addOnSuccessListener {
                    _userSaves.value = _userSaves.value.filterNot { save -> saves.any { it.id == save.id } }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }

    fun getSavedByPostAndUser(postId: String, userUid: String, callback: (SaveEntityFirebase?) -> Unit) {
        viewModelScope.launch {
            val saved = repository.getSavedByPostIdAndUserUid(postId, userUid)
            callback(saved)
        }
    }

    fun getSavedByUser(userUid: String): Flow<List<SaveEntityFirebase>> = callbackFlow {
        val listener = collection
            .whereEqualTo("userUid", userUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val saves = snapshot?.documents?.mapNotNull {
                    it.toObject(SaveEntityFirebase::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(saves)
            }

        awaitClose { listener.remove() }
    }

    fun insert(saveEntity: SaveEntityFirebase) {
        viewModelScope.launch {
            repository.insert(saveEntity)
        }
    }

    fun update(saveEntity: SaveEntityFirebase) {
        viewModelScope.launch {
            repository.update(saveEntity)
        }
    }

    fun delete(saveEntity: SaveEntityFirebase) {
        viewModelScope.launch {
            repository.delete(saveEntity)
        }
    }
}