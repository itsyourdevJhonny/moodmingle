package com.emc.moodmingle.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.BottomSheetDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.normal.PostEntityFirebase
import com.emc.moodmingle.domain.remote.model.notification.NotificationEntity
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.domain.local.model.post.formatTimeAgo
import com.emc.moodmingle.ui.notification.NotificationUtils
import com.emc.moodmingle.ui.post.text.ExpandableAutoDetectClickableText
import com.emc.moodmingle.ui.settings.saved.utils.EmptyComponent
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.TertiaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.remote.PostViewModelFirebase
import com.emc.moodmingle.viewmodel.remote.notification.NotificationViewModel
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

@Composable
private fun Content() {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val notificationViewModel = hiltViewModel<NotificationViewModel>()
    val postViewModel = hiltViewModel<PostViewModelFirebase>()

    val notificationUtils = NotificationUtils()

    val currentUser = userViewModel.loggedUser.value

    val notifications by remember(currentUser?.uid) {
        notificationViewModel.getNotificationsByUserId(currentUser?.uid ?: "")
    }.collectAsState(initial = emptyList())

    val sortedNotifications = remember(notifications) {
        notifications.sortedWith(
            compareByDescending<NotificationEntity?> { it?.pinned }
                .thenByDescending { it?.timestamp }
        )
    }

    TotalNotifications(notifications)

    if (notifications.isEmpty()) {
        EmptyComponent(R.drawable.no_collections, "You have no notifications yet.")
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = sortedNotifications,
            key = { it!!.id }
        ) { notification ->
            val lastUserId = notification?.users[notification.users.size - 1]

            val entity by remember(notification?.entityId ?: "") {
                if (
                    notification?.type != "FOLLOWED" &&
                    notification?.type != "UNFOLLOWED" &&
                    notification?.type != "UNSUPPORTED" &&
                    notification?.type != "SUPPORTED"
                ) {
                    postViewModel.getPostById(notification?.entityId ?: "")
                } else {
                    userViewModel.getUserByUid(notification.entityId)
                }
            }.collectAsState(initial = null)

            var lastUser by remember { mutableStateOf<UserEntityFirebase?>(null) }

            LaunchedEffect(lastUserId) {
                lastUser = userViewModel.getUserCached(lastUserId ?: "")
            }

            val notificationIcon = notificationUtils.getNotificationIcon(notification?.type)
            val notificationText = notificationUtils.getNotificationText(notification?.type)
            val notificationColor = notificationUtils.getNotificationColor(notification?.type)

            var showActionsSheet by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .clickable {}
                    .pointerInput(Unit) {
                        detectTapGestures(onLongPress = { showActionsSheet = true })
                    }
                    .border(
                        width = 0.5.dp,
                        color = TertiaryDark,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .animateItem(
                        fadeInSpec = spring(stiffness = Spring.StiffnessMedium),
                        placementSpec = spring(stiffness = Spring.StiffnessMedium),
                        fadeOutSpec = spring(stiffness = Spring.StiffnessMedium)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                NotificationTextAndTimestamp(
                                    notification,
                                    notificationText,
                                    lastUser,
                                    currentUser
                                )

                                NotificationContent(entity, notification?.type ?: "")
                            }
                        }

                        NotificationIcon(
                            notificationColor,
                            notificationIcon,
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )

                        PinnedIcon(notification)
                    }
                }
            }

            if (showActionsSheet) {
                ActionsSheet(notification) { showActionsSheet = false }
            }
        }
    }
}

@Composable
private fun TotalNotifications(notifications: List<NotificationEntity?>) {
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
}

@Composable
private fun BoxScope.PinnedIcon(notification: NotificationEntity?) {
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.TopEnd),
        visible = notification?.pinned == true,
        enter = fadeIn(animationSpec = tween(durationMillis = 300)),
        exit = fadeOut(animationSpec = tween(durationMillis = 300))
    ) {
        Icon(
            painter = painterResource(R.drawable.pin),
            contentDescription = "Pin",
            modifier = Modifier
                .size(20.dp),
            tint = Color.Red
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionsSheet(notification: NotificationEntity?, onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    val notificationViewModel = hiltViewModel<NotificationViewModel>()

    val isRead = notification?.read
    val isPinned = notification?.pinned

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = PrimaryDark,
        dragHandle = { BottomSheetDefaults.DragHandle(modifier = Modifier.drawGradient()) }
    ) {
        Column {
            Action(
                iconId = if (isPinned == true) R.drawable.unpin else R.drawable.pin,
                text = if (isPinned == true) "Unpin" else "Pin",
                onClick = {
                    notificationViewModel.updateNotification(notification!!.copy(pinned = !notification.pinned))
                    onDismiss()
                }
            )

            Action(
                iconId = if (isRead == true) R.drawable.visibility_off else R.drawable.view,
                text = "Mark as ${if (isRead == true) "Unread" else "Read"}",
                onClick = {
                    notificationViewModel.updateNotification(notification!!.copy(read = !notification.read))
                    onDismiss()
                }
            )

            Action(R.drawable.report, "Report") {
                onDismiss()
            }

            Action(
                iconId = R.drawable.remove,
                text = "Delete",
                onClick = {
                    scope.launch {
                        notificationViewModel.deleteNotification(notification!!)
                        onDismiss()
                    }
                }
            )
        }
    }
}

@Composable
private fun Action(@DrawableRes iconId: Int, text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.clickable { onClick() }
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
                modifier = Modifier.size(24.dp),
                tint = if (text == "Delete") Color.Red else Color.White
            )

            Text(
                text = text,
                style = Typography.bodyMedium.copy(color = Color.White)
            )
        }
    }
}

@Composable
private fun NotificationContent(entity: Any?, notificationType: String) {
    Box(
        modifier = Modifier
            .padding(top = 4.dp)
            .fillMaxWidth()
            .background(SecondaryDark, RoundedCornerShape(16.dp))
            .border(width = 0.5.dp, color = TertiaryDark, shape = RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            entity?.let {
                var user: UserEntityFirebase? = null
                var post: PostEntityFirebase? = null
                var avatarUrl: String

                when (notificationType) {
                    "FOLLOWED", "UNFOLLOWED", "UNSUPPORTED", "SUPPORTED" -> {
                        user = (it as Result<*>).getOrNull() as UserEntityFirebase?
                        avatarUrl = user?.avatarUrl ?: ""
                    }

                    else -> {
                        post = it as PostEntityFirebase
                        avatarUrl = post.avatarUrl
                    }
                }

                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .border(width = 0.5.dp, brush = BrushPrimaryGradient, shape = CircleShape),
                    contentScale = ContentScale.Crop
                )

                when (notificationType) {
                    "FOLLOWED", "UNFOLLOWED", "UNSUPPORTED", "SUPPORTED" -> {
                        SocializationInformation(user)
                    }

                    else -> PostInformation(post)
                }
            }
        }
    }
}

@Composable
private fun PostInformation(post: PostEntityFirebase?) {
    val information = listOf(
        post?.hashtag to R.drawable.hashtag,
        post?.caption to R.drawable.caption,
        post?.description to R.drawable.description
    )

    information.forEach { (text, iconRes) ->
        PostInformation(iconRes, text ?: "")
    }
}

@Composable
private fun SocializationInformation(user: UserEntityFirebase?) {
    val socialization = listOf(
        user?.followerIds?.size to "Followers",
        user?.followingIds?.size to "Following",
        user?.supporterIds?.size to "Supporters"
    )

    Column {
        socialization.forEach { (count, text) ->
            Socialization(count, text)
        }
    }
}

@Composable
private fun Socialization(count: Int?, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$count ",
            style = Typography.bodySmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
        )

        Text(
            text = text,
            style = Typography.bodySmall.copy(color = GrayTextColor)
        )
    }
}

@Composable
private fun NotificationTextAndTimestamp(
    notification: NotificationEntity?,
    notificationText: String,
    lastUser: UserEntityFirebase?,
    currentUser: UserEntityFirebase?
) {
    val prefix = if (lastUser == currentUser) "You" else lastUser?.username

    notification?.users?.size?.let {
        Text(
            text = "$prefix ${if (it > 1) "and ${notification.users.size} others " else ""}$notificationText",
            style = Typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }

    Text(
        text = formatTimeAgo(notification?.timestamp ?: 0),
        style = Typography.bodySmall.copy(color = GrayTextColor),
    )
}

@Composable
private fun NotificationIcon(
    notificationColor: Color,
    notificationIcon: Int,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(notificationIcon),
        contentDescription = "Icon",
        modifier = modifier
            .size(24.dp)
            .offset(y = 6.dp),
        tint = notificationColor
    )
}

@Composable
private fun PostInformation(@DrawableRes iconRes: Int, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = "Information",
            modifier = Modifier
                .size(16.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawGradient()
        )

        ExpandableAutoDetectClickableText(
            fullText = text,
            style = Typography.bodySmall.copy(color = GrayTextColor),
            hasPadding = false
        )
    }
}