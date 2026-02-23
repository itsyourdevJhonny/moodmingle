package com.emc.moodmingle.ui.post.action

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.normal.ShareEntityFirebase
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.domain.remote.model.notification.NotificationEntity
import com.emc.moodmingle.di.AppDatabase
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.remote.ShareViewModelFirebase
import com.emc.moodmingle.viewmodel.remote.notification.NotificationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareAction(
    onShowShareSheet: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int = R.drawable.share,
    boxModifier: Modifier = Modifier
) {
    Box(
        modifier = boxModifier
            .size(40.dp)
            .clickable { onShowShareSheet(true) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = "Share",
            tint = Color.White,
            modifier = modifier.size(20.dp)
        )
    }
}

@Composable
fun ShareTitle(isShared: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(end = 8.dp),
            text = if (isShared) "You’ve already shared this post" else "You are sharing this post...",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun DrawLine(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier
            .padding(top = 8.dp, bottom = 16.dp)
            .fillMaxWidth()
            .graphicsLayer(alpha = 0.99f)
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    drawRect(
                        brush = BrushPrimaryGradient,
                        blendMode = BlendMode.SrcAtop
                    )
                }
            },
        thickness = 1.dp,
        color = Color.White
    )
}

@Composable
fun DrawNoPaddingLine(modifier: Modifier = Modifier, thickness: Dp = 1.dp) {
    HorizontalDivider(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer(alpha = 0.99f)
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    drawRect(
                        brush = BrushPrimaryGradient,
                        blendMode = BlendMode.SrcAtop
                    )
                }
            },
        thickness = thickness,
        color = Color.White
    )
}

@Composable
fun PostInformation(@DrawableRes iconRes: Int, text: String, style: TextStyle) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            painter = painterResource(iconRes),
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush = BrushPrimaryGradient,
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                },
            contentDescription = "Information",
        )

        Text(text = text, color = Color.White, style = style)
    }
}

@Composable
fun PostInteractions(
    userEntity: UserEntityFirebase?,
    totalReactions: Long,
    totalComments: Long,
    totalShares: Long
) {
    Text(
        modifier = Modifier.padding(top = 16.dp),
        text = "Post Interactions",
        style = MaterialTheme.typography.titleSmall
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = userEntity?.avatarUrl ?: "",
            contentDescription = "Avatar",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatText(userEntity?.username ?: "", 29),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            PostCountStatistic(totalReactions, "Reaction", R.drawable.love)
            PostCountStatistic(totalComments, "Comment", R.drawable.comment)
            PostCountStatistic(totalShares, "Share", R.drawable.share)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ShareButton(
    onShowSheet: (Boolean) -> Unit,
    shareEntity: ShareEntityFirebase?,
    shareViewModel: ShareViewModelFirebase,
    postId: String,
    username: String?,
    isLoading: Boolean,
    onLoading: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val isShared = shareEntity != null

    val userDao = remember { AppDatabase.getDatabase(context).userDao() }
    var currentUserId by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userDao.getLoggedUser()?.uid?.let { currentUserId = it }
    }

    Button(
        onClick = { onLoading(true) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .background(BrushPrimaryGradient, CircleShape),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        if (isLoading) {
            ShareButtonLoadingIndicator()
        } else {
            ShareButtonIcon(isShared)
        }

        ShareButtonText(isLoading, isShared)
    }
}

fun executeShareOperation(
    isShared: Boolean,
    shareEntity: ShareEntityFirebase?,
    shareViewModel: ShareViewModelFirebase,
    notificationViewModel: NotificationViewModel,
    userUid: String,
    postId: String,
    username: String?,
    context: Context,
    scope: CoroutineScope,
    postUserId: String,
) {
    if (isShared) {
        shareViewModel.delete(shareEntity!!)
    } else {
        shareViewModel.insert(
            ShareEntityFirebase(
                userUid = userUid,
                postId = postId,
                time = System.currentTimeMillis()
            )
        )

        scope.launch {
            val userNotification = notificationViewModel.getNotificationByEntityId(postId)

            if (userNotification == null) {
                val newNotification = NotificationEntity(
                    userId = postUserId,
                    entityId = postId,
                    users = listOf(userUid),
                    type = "SHARE"
                )

                notificationViewModel.createNotification(newNotification)
            } else {
                val isExists =
                    userNotification.users.contains(userUid)

                if (isExists) {
                    notificationViewModel.updateNotification(
                        userNotification.copy(timestamp = System.currentTimeMillis())
                    )
                } else {
                    notificationViewModel.updateNotification(
                        userNotification.copy(
                            users = userNotification.users + userUid,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }
        }
    }

    Toast.makeText(
        context,
        "Post from $username ${if (isShared) "unshared" else "shared"}.",
        Toast.LENGTH_LONG
    ).show()
}

@Composable
fun ShareButtonLoadingIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.size(28.dp),
        color = Color.White,
        strokeWidth = 2.dp
    )
}

@Composable
fun ShareButtonIcon(isShared: Boolean) {
    Icon(
        modifier = Modifier.size(28.dp),
        painter = painterResource(if (isShared) R.drawable.remove else R.drawable.share),
        contentDescription = if (isShared) "Unshare" else "Share",
        tint = Color.White
    )
}

@Composable
fun ShareButtonText(isLoading: Boolean, isShared: Boolean) {
    Text(
        modifier = Modifier.padding(start = 8.dp),
        text = if (isLoading) {
            if (isShared) "Unsharing..." else "Sharing..."
        } else {
            if (isShared) "Unshare" else "Share"
        },
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun PostCountStatistic(count: Long, text: String, @DrawableRes iconRes: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = "Count",
            modifier = Modifier
                .size(14.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawGradient()
        )

        val suffix = if (count > 1) text + "s" else text

        Text(
            text = "$count $suffix",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

fun formatText(text: String, length: Int): String {
    return if (text.length > length) text.substring(0, length) + "..." else text
}