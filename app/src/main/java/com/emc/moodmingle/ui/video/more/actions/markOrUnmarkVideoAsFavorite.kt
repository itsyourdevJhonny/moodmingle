package com.emc.moodmingle.ui.video.more.actions

import android.content.Context
import android.widget.Toast
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun markOrUnmarkVideoAsFavorite(
    currentUser: UserEntityFirebase?,
    videoUrl: String,
    scope: CoroutineScope,
    userViewModel: FirebaseUserViewModel,
    context: Context
) {
    val isFavorite = currentUser?.favoriteVideoUrls?.contains(videoUrl)
    val updatedFavoriteVideoUrls =
        if (isFavorite == true) currentUser.favoriteVideoUrls - videoUrl else currentUser!!.favoriteVideoUrls + videoUrl

    scope.launch {
        userViewModel.updateUser(userEntity = currentUser.copy(favoriteVideoUrls = updatedFavoriteVideoUrls))
    }

    Toast.makeText(
        context, "Video ${if (isFavorite == true) "removed from" else "added to"} favorites",
        Toast.LENGTH_SHORT
    ).show()
}