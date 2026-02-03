package com.emc.moodmingle.ui.post.share

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.viewmodel.post.normal.NormalPostViewModel
import com.emc.moodmingle.ui.create.post.HashtagSection
import com.emc.moodmingle.ui.create.post.MentionSection
import com.emc.moodmingle.ui.create.post.TextSection
import com.emc.moodmingle.ui.post.MoodCard
import com.emc.moodmingle.ui.post.action.toastMessage
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.BackIcon
import com.emc.moodmingle.utils.media.MediaThumbnails
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.utils.text.NumberFormatter
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareSheet(
    entityId: String,
    type: String,
    isShared: Boolean,
    onShare: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isLoading by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Black,
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .background(Color.Black)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SheetHeader(isShared, onDismiss)

            HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.drawGradient())

            when (type) {
                "NORMAL_POST" -> PreviewNormalPost(entityId)
            }

            ShareButton(isShared, isLoading) { isLoading = it }
        }
    }

    if (isLoading) {
        LaunchedEffect(Unit) {
            delay(2000)
            onShare()

            toastMessage(
                context,
                message = when (type) {
                    "NORMAL_POST" -> "Post Shared"
                    else -> ""
                }
            )
        }
    }
}

@Composable
private fun SheetHeader(isShared: Boolean, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        BackIcon(onClick = onDismiss)

        Text(
            text = if (isShared) "You’ve already shared this post" else "You are sharing this post...",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun PreviewNormalPost(entityId: String) {
    val normalPostViewModel = hiltViewModel<NormalPostViewModel>()
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()

    val normalPost by remember(entityId) {
        normalPostViewModel.getPostById(entityId)
    }.collectAsState(initial = null)

    val user by remember(normalPost) {
        userViewModel.getUserById(normalPost?.userId.orEmpty())
    }.collectAsState(initial = null)

    normalPost?.let { post ->
        Column(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = user?.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .gradientCircleBorder(),
                        contentScale = ContentScale.Crop
                    )

                    Text(
                        text = user?.username.orEmpty(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                MoodCard(mood = post.mood, primaryColor = Color.White, secondaryColor = Color.Black)
            }

            if (post.mentionedUserIds.isNotEmpty()) {
                MentionSection(post)
            }

            if (post.description.text.isNotBlank()) {
                TextSection(post)
            }

            if (post.hashtag.isNotBlank() && post.hashtag != "#") {
                HashtagSection(post)
            }

            if (post.urls.isNotEmpty()) {
                MediaThumbnails(
                    urls = post.urls,
                    containerShape = RoundedCornerShape(8.dp)
                )
            }

            HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(top = 8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                listOf(
                    Triple(R.drawable.love, "Reactions", post.reactorIds.size),
                    Triple(R.drawable.comment, "Comments", post.commenterIds.size),
                    Triple(R.drawable.share, "Shares", post.sharerIds.size)
                ).forEach { (icon, label, count) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = label,
                            modifier = Modifier
                                .size(16.dp)
                                .drawGradient()
                        )

                        Text(
                            text = NumberFormatter.formatValue(count.toLong(), true),
                            style = Typography.bodySmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Text(text = label, style = Typography.bodySmall.copy(color = GrayTextColor))
                    }
                }
            }
        }
    }
}

