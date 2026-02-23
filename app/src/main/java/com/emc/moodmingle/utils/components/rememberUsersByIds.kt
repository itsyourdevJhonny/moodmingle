package com.emc.moodmingle.utils.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel
import kotlinx.coroutines.flow.first

@Composable
fun rememberUsersByIds(
    ids: List<String>?,
    viewModel: FirebaseUserViewModel
): State<List<UserEntityFirebase>> {
    return produceState(initialValue = emptyList(), ids) {
        value = ids?.mapNotNull { id ->
            viewModel.getUserById(id).first()
        } ?: emptyList()
    }
}