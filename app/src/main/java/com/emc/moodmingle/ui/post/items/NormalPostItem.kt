package com.emc.moodmingle.ui.post.items

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.domain.remote.viewmodel.post.normal.NormalPostViewModel
import com.emc.moodmingle.ui.create.post.EventSection
import com.emc.moodmingle.ui.create.post.HashtagSection
import com.emc.moodmingle.ui.create.post.MentionSection
import com.emc.moodmingle.ui.create.post.TextSection
import com.emc.moodmingle.ui.post.PostHeader
import com.emc.moodmingle.ui.post.PostMedia
import com.emc.moodmingle.ui.post.action.PostActions
import com.emc.moodmingle.ui.post.more.MoreSheet
import com.emc.moodmingle.ui.post.share.ShareSheet
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.viewmodel.remote.CombinedPost
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel

@Composable
fun NormalPostItem(
    currentUserId: String,
    combinedPost: CombinedPost,
    userViewModel: FirebaseUserViewModel,
    onChat: (String, String) -> Unit
) {
    val normalPostViewModel = hiltViewModel<NormalPostViewModel>()

    val normalPost by remember(combinedPost) {
        normalPostViewModel.getPostById(combinedPost.id)
    }.collectAsState(initial = null)

    val user by remember(normalPost) {
        userViewModel.getUserById(normalPost?.userId.orEmpty())
    }.collectAsState(initial = null)

    var openShareSheet by remember { mutableStateOf(false) }
    var openMoreSheet by remember { mutableStateOf(false) }

    normalPost?.let { post ->
        Column(
            modifier = Modifier
                .padding(top = 4.dp)
                .background(PrimaryDark)
                .animateContentSize()
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PostHeader(
                    user = user,
                    tagUserIds = post.taggedUserIds,
                    primaryColor = Color.White,
                    secondaryColor = Color.Black,
                    mood = post.mood,
                    location = post.location,
                    onMore = { openMoreSheet = true }
                )

                MentionSection(post)
                TextSection(post)
                HashtagSection(post)
            }

            if (post.urls.isNotEmpty()) {
                PostMedia(mediaUrls = post.urls, onShowShareSheet = { })
            }

            EventSection(post)

            PostActions(
                entityId = combinedPost.id,
                ownerId = post.userId,
                isReacted = post.reactorIds.contains(currentUserId),
                reactions = post.reactorIds.size.toLong(),
                comments = post.commenterIds.size.toLong(),
                shares = post.sharerIds.size.toLong(),
                onReact = {
                    normalPostViewModel.updatePost(
                        postEntity = post.copy(
                            reactorIds = if (post.reactorIds.contains(currentUserId)) post.reactorIds - currentUserId else post.reactorIds + currentUserId
                        )
                    )
                },
                onComment = {},
                onShare = { openShareSheet = true },
                onChat = onChat
            )
        }

        if (openShareSheet) {
            ShareSheet(
                entityId = post.id,
                type = "NORMAL_POST",
                isShared = post.sharerIds.contains(currentUserId),
                onShare = {
                    normalPostViewModel.updatePost(
                        postEntity = post.copy(
                            sharerIds = if (post.sharerIds.contains(currentUserId)) post.sharerIds - currentUserId else post.sharerIds + currentUserId
                        )
                    )

                    openShareSheet = false
                    if (openMoreSheet) openMoreSheet = false
                },
                onDismiss = { openShareSheet = false }
            )
        }

        if (openMoreSheet) {
            user?.let { owner ->
                MoreSheet(
                    currentUserId,
                    owner,
                    entityId = post.id,
                    onChat,
                    onShare = { openShareSheet = true },
                    onDismissMoreSheet = { openMoreSheet = false }
                )
            }
        }
    }
}