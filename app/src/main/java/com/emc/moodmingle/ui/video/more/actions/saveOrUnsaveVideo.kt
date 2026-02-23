package com.emc.moodmingle.ui.video.more.actions

import android.content.Context
import android.widget.Toast
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun saveOrUnsaveVideo(
    currentUser: UserEntityFirebase?,
    videoUrl: String,
    scope: CoroutineScope,
    userViewModel: FirebaseUserViewModel,
    context: Context
) {
    val isSaved = currentUser?.savedVideoUrls?.contains(videoUrl)
    val updatedVideoUrls =
        if (isSaved == true) currentUser.savedVideoUrls - videoUrl else currentUser!!.savedVideoUrls + videoUrl

    scope.launch {
        userViewModel.updateUser(
            userEntity = currentUser.copy(savedVideoUrls = updatedVideoUrls)
        )
    }

    Toast.makeText(
        context, "Video ${if (isSaved == true) "unsaved" else "saved"} successfully",
        Toast.LENGTH_SHORT
    ).show()

}