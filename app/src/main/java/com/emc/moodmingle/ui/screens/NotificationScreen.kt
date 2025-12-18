package com.emc.moodmingle.ui.screens

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.UserEntityFirebase
import com.emc.moodmingle.data.model.post.formatTimeAgo
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.firebase.PostViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.notification.NotificationViewModel
import kotlinx.coroutines.launch

@Composable
fun NotificationScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryDark)
    ) {
        Header(onBack)
        Content()
    }
}

@Composable
private fun Header(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.clickable { onBack() },
            tint = Color.White
        )

        Text(
            text = "Notifications",
            style = Typography.bodyLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.W900
            )
        )
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun Content() {
    val scope = rememberCoroutineScope()
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val notificationViewModel = hiltViewModel<NotificationViewModel>()
    val postViewModel = hiltViewModel<PostViewModelFirebase>()

    val currentUser = userViewModel.loggedUser.value
    val notifications by remember(currentUser?.uid) {
        notificationViewModel.getNotificationsByUserId(currentUser?.uid ?: "")
    }.collectAsState(initial = emptyList())

    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifications",
            modifier = Modifier
                .graphicsLayer(alpha = 0.99f)
                .drawGradient()
        )

        Text(
            text = "${notifications.size} total notifications",
            style = Typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(notifications.sortedByDescending { it?.timestamp }) { notification ->
            val lastUserId = notification?.users[notification.users.size - 1]
            var lastUser by remember { mutableStateOf<UserEntityFirebase?>(null) }
            val post by remember(notification?.postId ?: "") {
                postViewModel.getPostById(notification?.postId ?: "")
            }.collectAsState(initial = null)

            LaunchedEffect(lastUserId) {
                lastUser = userViewModel.getUserCached(lastUserId ?: "")
            }

            val notificationIcon = when (notification?.type) {
                "POST_CHAT" -> R.drawable.chat
                "SHARE" -> R.drawable.share
                "COMMENT" -> R.drawable.comment
                "SAVE" -> R.drawable.save_post
                else -> R.drawable.love
            }

            val notificationText = when (notification?.type) {
                "POST_CHAT" -> "messaged on your post"
                "SHARE" -> "shared your post"
                "COMMENT" -> "commented on your post"
                "SAVE" -> "saved your post"
                else -> "reacted to your post"
            }

            val notificationColor = when (notification?.type) {
                "POST_CHAT" -> Color.Blue
                "SHARE" -> Color.Cyan.copy(alpha = 0.6f)
                "COMMENT" -> Color.Green.copy(alpha = 0.6f)
                "SAVE" -> Color.Yellow
                else -> Color.Red
            }

            Box(modifier = Modifier.clickable {}) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(notificationColor, CircleShape)
                                .size(38.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(notificationIcon),
                                contentDescription = "Icon",
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val prefix = if (lastUser == currentUser) "You" else lastUser?.username

                            notification?.users?.size?.let {
                                Text(
                                    text = "$prefix ${if (it > 1) "and ${notification.users.size} others " else ""}$notificationText",
                                    style = Typography.bodyLarge.copy(color = Color.White),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }

                            Text(
                                text = formatTimeAgo(notification?.timestamp ?: 0),
                                style = Typography.bodySmall.copy(color = GrayTextColor),
                            )

                            Box(
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .fillMaxWidth()
                                    .background(SecondaryDark, RoundedCornerShape(8.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    post?.let {
                                        AsyncImage(
                                            model = it.avatarUrl,
                                            contentDescription = "Avatar",
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .border(
                                                    width = 0.5.dp,
                                                    brush = BrushPrimaryGradient,
                                                    shape = CircleShape
                                                ),
                                            contentScale = ContentScale.Crop
                                        )

                                        Column {
                                            PostInformation(R.drawable.hashtag, it.hashtag)
                                            PostInformation(R.drawable.caption, it.caption)
                                            PostInformation(R.drawable.description, it.description)
                                        }
                                    }
                                }
                            }

                            Icon(
                                painter = painterResource(R.drawable.remove),
                                contentDescription = "Remove",
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .size(20.dp)
                                    .clickable {
                                        scope.launch {
                                            notificationViewModel.deleteNotification(notification!!)
                                        }
                                    },
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PostInformation(@DrawableRes iconRes: Int, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = "Information",
            modifier = Modifier
                .size(16.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawGradient()
        )

        Text(
            text = text,
            style = Typography.bodySmall.copy(color = GrayTextColor),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}