package com.emc.moodmingle.ui.video

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.data.firebase.viewmodel.video.VideoCommentViewModel
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.ui.video.more.VideoMoreBottomSheet
import com.emc.moodmingle.ui.video.operations.performVideoReactionOperation
import com.emc.moodmingle.ui.video.operations.performVideoRepostOperation
import com.emc.moodmingle.utils.text.NumberFormatter
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.firebase.ReactionViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.notification.NotificationViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@Composable
fun VideoItemInteractionContent(post: PostEntityFirebase, videoUrl: String, onCommentBottomSheet: (Boolean) -> Unit) {
    val videoCommentViewModel = hiltViewModel<VideoCommentViewModel>()

    var showMoreBottomSheet by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        InteractionIcon(R.drawable.love, post, videoUrl)
        InteractionIcon(R.drawable.comment, post, videoUrl, videoCommentViewModel) {
            onCommentBottomSheet(it)
        }
        InteractionIcon(R.drawable.repost, post, videoUrl)
        InteractionIcon(R.drawable.more, post, videoUrl) {
            showMoreBottomSheet = it
        }
    }

    if (showMoreBottomSheet) {
        VideoMoreBottomSheet(onDismiss = { showMoreBottomSheet = false }, post, videoUrl)
    }
}

@Composable
private fun InteractionIcon(
    @DrawableRes iconRes: Int,
    post: PostEntityFirebase,
    videoUrl: String,
    videoCommentViewModel: VideoCommentViewModel = hiltViewModel(),
    onShowBottomSheet: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val notificationViewModel = hiltViewModel<NotificationViewModel>()
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val reactionViewModel = hiltViewModel<ReactionViewModelFirebase>()

    val currentUser by userViewModel.loggedUser
    val currentUserId = currentUser?.uid ?: ""
    val postId = post.id

    val currentUserReaction by remember(currentUserId, postId) {
        reactionViewModel.getReactionByReactorIdAndPostId(currentUserId, postId)
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), null)
    }.collectAsState()

    val scale = remember { Animatable(1f) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color.Black.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = "Interaction",
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer {
                        if (iconRes == R.drawable.love) {
                            scaleX = scale.value
                            scaleY = scale.value
                        }
                    }
                    .clickable {
                        when (iconRes) {
                            R.drawable.love -> {
                                performVideoReactionOperation(
                                    currentUserReaction,
                                    scope,
                                    reactionViewModel,
                                    postId,
                                    currentUserId,
                                    notificationViewModel,
                                    post,
                                    currentUser,
                                    scale
                                )
                            }

                            R.drawable.repost -> {
                                performVideoRepostOperation(
                                    currentUser,
                                    post,
                                    scope,
                                    userViewModel,
                                    videoUrl,
                                    context
                                )
                            }

                            R.drawable.comment, R.drawable.more -> onShowBottomSheet(true)
                        }
                    },
                tint = when (iconRes) {
                    R.drawable.love -> if (currentUserReaction != null) Color.Red else Color.White
                    R.drawable.repost -> {
                        val isReposted = currentUser?.reposts?.any { it.videoUrl == videoUrl }
                        if (isReposted == true) PurplePrimary else Color.White
                    }

                    else -> Color.White
                }
            )
        }

        when (iconRes) {
            R.drawable.love -> VideoReactionsCount(postId, reactionViewModel)
            R.drawable.comment -> VideoCommentsCount(videoUrl, videoCommentViewModel)
        }
    }
}

@Composable
private fun VideoCommentsCount(videoUrl: String, videoCommentViewModel: VideoCommentViewModel) {
    val commentCount by remember(videoUrl) {
        videoCommentViewModel.getCommentCountByVideoUrl(videoUrl)
    }.collectAsState(initial = 0)

    if (commentCount > 0) {
        Box(
            modifier = Modifier.background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
        ) {
            Text(
                text = NumberFormatter.formatValue(commentCount, true),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun VideoReactionsCount(postId: String, reactionViewModel: ReactionViewModelFirebase) {
    val reactionCount by remember(postId) {
        reactionViewModel.getReactionsCountByPostId(postId)
    }.collectAsState(initial = 0)

    if (reactionCount > 0) {
        Box(
            modifier = Modifier.background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
        ) {
            Text(
                text = NumberFormatter.formatValue(reactionCount, true),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}