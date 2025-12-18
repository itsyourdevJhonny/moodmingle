package com.emc.moodmingle.viewmodel.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.data.firebase.model.HideEntityFirebase
import com.emc.moodmingle.data.firebase.repository.HideRepositoryFirebase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HideViewModelFirebase @Inject constructor(
    private val repository: HideRepositoryFirebase,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val collection = firestore.collection("hide")
    private val _userHides = MutableStateFlow<List<HideEntityFirebase>>(emptyList())
    val userHides: StateFlow<List<HideEntityFirebase>> = _userHides

    fun loadHiddenByUser(userUid: String) {
        collection
            .whereEqualTo("userUid", userUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                _userHides.value = snapshot?.documents?.map { doc ->
                    doc.toObject(HideEntityFirebase::class.java)!!
                } ?: emptyList()
            }
    }

    fun getHiddenByPostAndUser(postId: String, userUid: String, callback: (HideEntityFirebase?) -> Unit) {
        viewModelScope.launch {
            val hide = repository.getHiddenByPostIdAndUserUid(postId, userUid)
            callback(hide)
        }
    }

    fun insert(hideEntity: HideEntityFirebase) {
        viewModelScope.launch {
            repository.insert(hideEntity)
        }
    }

    fun update(hideEntity: HideEntityFirebase) {
        viewModelScope.launch {
            repository.update(hideEntity)
        }
    }

    fun delete(hideEntity: HideEntityFirebase) {
        viewModelScope.launch {
            repository.delete(hideEntity)
        }
    }
}
