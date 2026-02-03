package com.emc.moodmingle.ui.video.more

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.post.getVideoThumbnail
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.TertiaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.ui.video.more.actions.hideVideo
import com.emc.moodmingle.ui.video.more.actions.markOrUnmarkVideoAsFavorite
import com.emc.moodmingle.ui.video.more.actions.saveOrUnsaveVideo
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.local.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoMoreBottomSheet(onDismiss: () -> Unit, post: PostEntityFirebase, videoUrl: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val currentUser by userViewModel.loggedUser
    val isFromCurrentUser = currentUser?.uid == post.userId

    val isSaved = currentUser?.savedVideoUrls?.contains(videoUrl)
    val isFavorite = currentUser?.favoriteVideoUrls?.contains(videoUrl)

    val actions = listOf(
        Triple(R.drawable.hide, "Hide") {
            hideVideo(scope, userViewModel, currentUser, videoUrl, context)
            onDismiss()
        },
        Triple(R.drawable.save_post, if (isSaved == true) "Unsave" else "Save") {
            saveOrUnsaveVideo(currentUser, videoUrl, scope, userViewModel, context)
            onDismiss()
        },
        Triple(
            R.drawable.add_to_favorite,
            if (isFavorite == true) "Remove from Favorites" else "Mark as Favorite"
        ) {
            markOrUnmarkVideoAsFavorite(currentUser, videoUrl, scope, userViewModel, context)
            onDismiss()
        },
        Triple(R.drawable.chat, "Talk with ${post.username}") {},
        Triple(R.drawable.report, "Report") {},
        Triple(R.drawable.share_other, "Share") {}
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = PrimaryDark,
        shape = RectangleShape,
        dragHandle = {
            if (!isFromCurrentUser) {
                Header(videoUrl, post)
            }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DrawNoPaddingLine(thickness = 0.5.dp)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .fillMaxWidth()
            ) {
                items(actions) { (iconId, text, onClick) ->
                    Action(iconId, text, onClick, isFromCurrentUser)
                }
            }
        }
    }
}

@Composable
private fun Header(videoUrl: String, post: PostEntityFirebase) {
    val postViewModel = hiltViewModel<PostViewModel>()
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()

    val userResult by remember(post.userId) {
        userViewModel.getUserByUid(post.userId)
    }.collectAsState(initial = null)

    val user = userResult?.getOrNull()

    val cachedThumbnail = postViewModel.post.getCachedThumbnail(videoUrl)
    var thumbnail by remember(videoUrl) { mutableStateOf(cachedThumbnail) }

    LaunchedEffect(videoUrl) {
        if (thumbnail == null) {
            val generated = getVideoThumbnail(videoUrl)
            if (generated != null) {
                postViewModel.post.cacheThumbnail(videoUrl, generated)
                thumbnail = generated
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        thumbnail?.asImageBitmap()?.let {
            Box(
                modifier = Modifier.padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = it,
                    contentDescription = "Thumbnail",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 0.5.dp,
                            brush = BrushPrimaryGradient,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentScale = ContentScale.Crop
                )

                Icon(
                    painter = painterResource(R.drawable.video),
                    contentDescription = "Video",
                    modifier = Modifier
                        .size(24.dp)
                        .drawGradient()
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = user?.avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .border(
                            width = 0.5.dp,
                            brush = BrushPrimaryGradient,
                            shape = CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = user?.username ?: "",
                    style = Typography.bodyMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (user?.verified == true) {
                    Icon(
                        painter = painterResource(R.drawable.verified),
                        contentDescription = "Verified"
                    )
                }
            }

            Box(
                modifier = Modifier.background(BrushPrimaryGradient, RoundedCornerShape(8.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.follow),
                        contentDescription = "Follow",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )

                    Text(
                        text = "Follow ${post.username}",
                        style = Typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun Action(
    @DrawableRes iconId: Int,
    text: String,
    onClick: () -> Unit,
    isFromCurrentUser: Boolean
) {
    Box(
        modifier = Modifier
            .background(SecondaryDark, CircleShape)
            .border(
                width = 0.3.dp,
                color = TertiaryDark,
                shape = CircleShape
            )
            .clickable {
                if (iconId == R.drawable.chat && isFromCurrentUser) return@clickable
                else onClick()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(iconId),
                contentDescription = "Action",
                modifier = Modifier
                    .size(24.dp)
                    .drawGradient(),
                tint = if (text == "Delete") Color.Red else Color.White
            )

            Text(
                text = text,
                style = Typography.bodySmall.copy(color = Color.White),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}