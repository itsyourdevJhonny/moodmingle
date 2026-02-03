package com.emc.moodmingle.ui.video.operations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.post.reaction.ReactionEntityFirebase
import com.emc.moodmingle.data.firebase.model.notification.NotificationEntity
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.viewmodel.firebase.ReactionViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.notification.NotificationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun performVideoReactionOperation(
    currentUserReaction: ReactionEntityFirebase?,
    scope: CoroutineScope,
    reactionViewModel: ReactionViewModelFirebase,
    postId: String,
    currentUserId: String,
    notificationViewModel: NotificationViewModel,
    post: PostEntityFirebase,
    currentUser: UserEntityFirebase?,
    scale: Animatable<Float, AnimationVector1D>
) {
    if (currentUserReaction != null) {
        scope.launch {
            reactionViewModel.deleteReaction(currentUserReaction)
        }
    } else {
        val newReaction = ReactionEntityFirebase(
            postId = postId,
            reactorId = currentUserId,
            reactionType = "HEART"
        )

        scope.launch {
            reactionViewModel.insertReaction(newReaction)

            val userNotification = notificationViewModel.getNotificationByEntityId(postId)

            if (userNotification == null) {
                val newNotification = NotificationEntity(
                    userId = post.userId,
                    entityId = postId,
                    users = listOf(currentUser?.uid ?: ""),
                    type = "REACTION"
                )

                notificationViewModel.createNotification(newNotification)
            } else {
                val isExists = userNotification.users.contains(currentUser?.uid ?: "")

                if (isExists) {
                    notificationViewModel.updateNotification(
                        notificationEntity = userNotification.copy(timestamp = System.currentTimeMillis())
                    )
                } else {
                    notificationViewModel.updateNotification(
                        notificationEntity = userNotification.copy(
                            users = userNotification.users + (currentUser?.uid ?: ""),
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }
        }

        scope.launch {
            scale.animateTo(
                1.5f,
                animationSpec = tween(500, easing = LinearOutSlowInEasing)
            )
            scale.animateTo(
                1f,
                animationSpec = tween(500, easing = LinearOutSlowInEasing)
            )
        }
    }
}