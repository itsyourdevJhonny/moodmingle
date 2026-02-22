package com.emc.moodmingle.ui.video.more.actions

import android.content.Context
import android.widget.Toast
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun hideVideo(
    scope: CoroutineScope,
    userViewModel: FirebaseUserViewModel,
    currentUser: UserEntityFirebase?,
    videoUrl: String,
    context: Context
) {
    scope.launch {
        userViewModel.updateUser(
            userEntity = currentUser!!.copy(
                hiddenVideoUrls = currentUser.hiddenVideoUrls + videoUrl
            )
        )
    }

    Toast.makeText(
        context, "You won't see this video again",
        Toast.LENGTH_SHORT
    ).show()
}