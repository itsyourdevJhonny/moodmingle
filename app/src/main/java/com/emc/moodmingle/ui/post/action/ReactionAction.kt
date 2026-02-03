package com.emc.moodmingle.ui.post.action

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.post.reaction.ReactionEntityFirebase
import com.emc.moodmingle.data.firebase.model.notification.NotificationEntity
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.firebase.ReactionViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.notification.NotificationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReactionAction(
    postEntity: PostEntityFirebase,
    reactionViewModel: ReactionViewModelFirebase,
    currentUserId: String,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val notificationViewModel = hiltViewModel<NotificationViewModel>()
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()

    val currentUser by userViewModel.loggedUser

    val localReaction by remember(currentUserId, postEntity.id) {
        reactionViewModel.getReactionByReactorIdAndPostId(currentUserId, postEntity.id)
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), null)
    }.collectAsState()

    val scale = remember { Animatable(1f) }
    val floatingHearts =
        remember { mutableStateListOf<Triple<Float, Animatable<Float, AnimationVector1D>, Float>>() }

    Box(
        modifier = modifier
            .size(40.dp)
            .background(SecondaryDark, CircleShape)
            .border(
                width = 1.dp,
                color = if (localReaction != null) Color.Red else Color.White,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.love),
            contentDescription = "Reaction Icon",
            modifier = Modifier
                .size(20.dp)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                }
                .combinedClickable(
                    onClick = {
                        if (localReaction != null) {
                            scope.launch {
                                reactionViewModel.deleteReaction(localReaction!!)
                            }
                        } else {
                            val newReaction = ReactionEntityFirebase(
                                postId = postEntity.id,
                                reactorId = currentUserId,
                                reactionType = "HEART"
                            )

                            scope.launch {
                                reactionViewModel.insertReaction(newReaction)

                                val userNotification =
                                    notificationViewModel.getNotificationByEntityId(entityId = postEntity.id)

                                if (userNotification == null) {
                                    val newNotification = NotificationEntity(
                                        userId = postEntity.userId,
                                        entityId = postEntity.id,
                                        users = listOf(currentUser?.uid ?: ""),
                                        type = "REACTION"
                                    )

                                    notificationViewModel.createNotification(newNotification)
                                } else {
                                    val isExists =
                                        userNotification.users.contains(currentUser?.uid ?: "")

                                    if (isExists) {
                                        notificationViewModel.updateNotification(
                                            userNotification.copy(timestamp = System.currentTimeMillis())
                                        )
                                    } else {
                                        notificationViewModel.updateNotification(
                                            userNotification.copy(
                                                users = userNotification.users + (currentUser?.uid
                                                    ?: ""),
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

                            repeat(16) { index ->
                                scope.launch {
                                    delay(index * 100L)

                                    val animY = Animatable(0f)
                                    val randomX = (-15..15).random().toFloat()
                                    val randomScale = 0.8f + Random.nextFloat() * 0.4f
                                    floatingHearts.add(Triple(randomX, animY, randomScale))

                                    animY.animateTo(
                                        targetValue = -150f - Random.nextFloat() * 30f,
                                        animationSpec = tween(
                                            durationMillis = 800 + Random.nextInt(
                                                200
                                            ), easing = LinearOutSlowInEasing
                                        )
                                    )

                                    floatingHearts.remove(Triple(randomX, animY, randomScale))
                                }
                            }
                        }
                    }
                ),
            tint = if (localReaction != null) Color.Red else Color.White
        )

        floatingHearts.forEach { (xOffset, animY, scaleFactor) ->
            Icon(
                painter = painterResource(R.drawable.love),
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer {
                        scaleX = scaleFactor
                        scaleY = scaleFactor
                    }
                    .offset(x = xOffset.dp, y = animY.value.dp)
            )
        }
    }
}