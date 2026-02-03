package com.emc.moodmingle.ui.video.operations

import android.content.Context
import android.widget.Toast
import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.user.Repost
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun performVideoRepostOperation(
    currentUser: UserEntityFirebase?,
    post: PostEntityFirebase,
    scope: CoroutineScope,
    userViewModel: FirebaseUserViewModel,
    videoUrl: String,
    context: Context
) {
    val isReposted = currentUser?.reposts?.any { it.post?.id == post.id }

    scope.launch {
        userViewModel.updateUser(
            userEntity = currentUser!!.copy(
                reposts = if (isReposted == true) {
                    val repost = currentUser.reposts.find { it.videoUrl == videoUrl }
                    currentUser.reposts - repost!!
                } else {
                    currentUser.reposts + Repost(post, videoUrl)
                }
            )
        )
    }

    Toast.makeText(
        context,
        if (isReposted == true) "Unreposted" else "Reposted",
        Toast.LENGTH_SHORT
    ).show()
}