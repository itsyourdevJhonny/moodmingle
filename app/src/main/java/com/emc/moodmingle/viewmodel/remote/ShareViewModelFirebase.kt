package com.emc.moodmingle.viewmodel.remote

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.domain.remote.model.post.normal.ShareEntityFirebase
import com.emc.moodmingle.domain.remote.repository.post.ShareRepositoryFirebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.orEmpty

/**
 * ViewModel for shares with Firebase.
 * exposes flow for real-time updates
 */
@HiltViewModel
class ShareViewModelFirebase @Inject constructor(
    private val repository: ShareRepositoryFirebase,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val sharesCollection = firestore.collection("shares")
    /**
     * insert a share
     */
    fun insert(shareEntity: ShareEntityFirebase) = viewModelScope.launch {
        repository.insert(shareEntity)
    }

    fun getAllShares() = repository.getAllShares()

    fun getSharesByPostId(postId: String): Flow<List<ShareEntityFirebase>> = callbackFlow {
        val listener = sharesCollection
            .whereEqualTo("postId", postId)
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val shares = snapshot?.toObjects(ShareEntityFirebase::class.java).orEmpty()
                trySend(shares)
            }

        awaitClose { listener.remove() }
    }

    /**
     * get shares by userUid as flow
     */
    fun getSharedByUserUid(userUid: String): StateFlow<List<ShareEntityFirebase>> =
        repository.getSharedByUserUid(userUid)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * get share by postId as flow
     */
    fun getSharedByPostId(postId: String): StateFlow<ShareEntityFirebase?> =
        repository.getSharedByPostId(postId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    suspend fun getSharedPostByPostIdOnce(postId: String) = repository.getSharedByPostIdOnce(postId)

    /**
     * get share by postId and userUid as flow
     */
    fun getSharedByPostIdAndUserUid(postId: String, userUid: String): StateFlow<ShareEntityFirebase?> {
        Log.d("MODEL", "${repository.getSharedByPostIdAndUserUid(postId, userUid)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null).value}")
        return repository.getSharedByPostIdAndUserUid(postId, userUid)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    /**
     * get real-time share count by postId as flow
     */
    fun getShareCountByPostId(postId: String): StateFlow<Long> =
        repository.getShareCountByPostId(postId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    /**
     * update a share
     */
    fun update(shareEntity: ShareEntityFirebase) = viewModelScope.launch {
        repository.update(shareEntity)
    }

    /**
     * delete a share
     */
    fun delete(shareEntity: ShareEntityFirebase) = viewModelScope.launch {
        repository.delete(shareEntity)
    }
}
