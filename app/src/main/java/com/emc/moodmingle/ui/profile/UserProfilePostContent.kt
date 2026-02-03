package com.emc.moodmingle.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.post.ShareEntityFirebase
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.data.model.post.formatTimeAgo
import com.emc.moodmingle.data.model.post.user.CombinedPost
import com.emc.moodmingle.data.model.post.user.PostType
import com.emc.moodmingle.ui.post.AvatarImage
import com.emc.moodmingle.ui.post.MultimediaCard
import com.emc.moodmingle.ui.post.PostMedia
import com.emc.moodmingle.ui.post.formatUsername
import com.emc.moodmingle.ui.post.skeleton.SharedPostSkeleton
import com.emc.moodmingle.ui.post.text.ExpandableAutoDetectClickableText
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.TertiaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.firebase.PostViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.ShareViewModelFirebase

@Composable
fun UserPostContent(post: CombinedPost, postViewModel: PostViewModelFirebase, onChatClick: (String, String) -> Unit) {
    if (post.type == PostType.SHARED_POST) {
        val shareEntity = post.shareEntity
        val postId = shareEntity!!.postId
        val context = LocalContext.current

        var postEntity by remember { mutableStateOf<PostEntityFirebase?>(null) }

        LaunchedEffect(postId) {
            postEntity = try {
                postViewModel.getPostByIdOnce(postId)
            } catch (_: Exception) {
                Toast.makeText(context, "Failed to load post", Toast.LENGTH_SHORT).show()
                null
            }
        }

        Column(
            modifier = Modifier
                .padding(top = 2.dp)
                .fillMaxWidth()
                .background(SecondaryDark),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                postEntity?.let { SharedUserInformation(shareEntity) }
            }

            if (postEntity == null) {
                SharedPostSkeleton()
            } else {
                RenderMultimediaContent(postEntity!!, onChatClick)
            }
        }
    } else {
        post.postEntity?.let { RenderMultimediaContent(it, onChatClick) }
    }
}

@Composable
fun RenderMultimediaContent(postEntity: PostEntityFirebase, onChatClick: (String, String) -> Unit) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    var userEntity by remember { mutableStateOf<UserEntityFirebase?>(null) }

    LaunchedEffect(postEntity.userId) {
        userEntity = userViewModel.getUserCached(postEntity.userId)
    }

    var showShareSheet by remember { mutableStateOf(false) }

    if (postEntity.urls.isNotEmpty()) {
        MultimediaCard(
            composable = {
                PostMedia(
                    mediaUrls = postEntity.urls,
                    onShowShareSheet = { showShareSheet = it }
                )
            },
            postEntity = postEntity,
            userEntity = userEntity,
            postType = "IMAGE",
            onClick = {},
            showShareSheet = showShareSheet,
            onShowShareSheet = { showShareSheet = it },
            onChatClick = onChatClick
        )
    } else {
        MultimediaCard(
            composable = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    content = {
                        ExpandableAutoDetectClickableText(
                            fullText = postEntity.description,
                            style = MaterialTheme.typography.bodyLarge,
                            hasPadding = false,
                        )
                    }
                )
            },
            postEntity = postEntity,
            userEntity = userEntity,
            postType = "TEXT",
            onClick = {},
            showShareSheet = showShareSheet,
            onShowShareSheet = { showShareSheet = it },
            onChatClick = onChatClick
        )
    }
}

@Composable
fun SharedUserInformation(shareEntity: ShareEntityFirebase) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val shareViewModel = hiltViewModel<ShareViewModelFirebase>()

    val userEntity by remember(shareEntity.userUid) { userViewModel.getUserByUid(shareEntity.userUid) }
        .collectAsState(initial = null)

    val shareEntity by remember(shareEntity.postId) { shareViewModel.getSharedByPostId(shareEntity.postId) }
        .collectAsState(initial = null)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            userEntity?.let { userResult ->
                val user = userResult
                AvatarImage(user.getOrNull()?.avatarUrl ?: "", {}, user.getOrNull()?.uid ?: "")

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = formatUsername(user.getOrNull()?.username ?: ""),
                        style = Typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 185.dp)
                    )

                    shareEntity?.let { share ->
                        Text(
                            text = formatTimeAgo(share.time),
                            fontSize = 12.sp,
                            color = GrayTextColor
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.background(TertiaryDark, RoundedCornerShape(8.dp))) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.share),
                    contentDescription = "Check",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = "Shared",
                    style = Typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}