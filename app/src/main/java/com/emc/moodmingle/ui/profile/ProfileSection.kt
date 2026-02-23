package com.emc.moodmingle.ui.profile

import android.content.Context
import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.notification.NotificationEntity
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.profile.storymood.StoryMood
import com.emc.moodmingle.ui.profile.storymood.add.AddStoryMood
import com.emc.moodmingle.ui.profile.utils.ShowProfilePicture
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.TertiaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.ui.theme.VerifiedColor
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.remote.notification.NotificationViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileSection(
    isFromOtherUser: Boolean,
    user: UserEntityFirebase?,
    postCount: Long,
    shareCount: Long,
    saveCount: Long,
    favoritesCount: Long,
) {
    val joinedDate = user?.joinedDate ?: ""

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            user?.let {
                Avatar(isFromOtherUser, user, postCount, shareCount, saveCount, favoritesCount)
                Username(currentUser = user)

                if (isFromOtherUser) {
                    SocializationButtons(user)
                }

                Follows(user)
                Supports(user)
            }

            CreateJoinedDate(joinedDate)
        }
    }
}

@Composable
private fun Avatar(
    isFromOtherUser: Boolean,
    user: UserEntityFirebase,
    postCount: Long,
    shareCount: Long,
    saveCount: Long,
    favoritesCount: Long,
) {
    val context = LocalContext.current
    var showProfilePicture by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        AvatarImage(context, user, isFromOtherUser) { showProfilePicture = true }

        Column {
            Interactions(postCount, shareCount, saveCount, favoritesCount)
            Bio(user.bio)
        }
    }

    if (showProfilePicture) {
        ShowProfilePicture(user.avatarUrl) { showProfilePicture = false }
    }
}

@Composable
private fun AvatarImage(
    context: Context,
    user: UserEntityFirebase,
    isFromOtherUser: Boolean,
    onAvatarClick: () -> Unit,
) {
    Box(contentAlignment = Alignment.TopCenter) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(user.avatarUrl)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.3f))
                .gradientCircleBorder()
                .clickable { onAvatarClick() }
        )

        if (isFromOtherUser) StoryMood() else AddStoryMood(modifier = Modifier.align(Alignment.BottomEnd))
    }
}

@Composable
private fun Bio(bio: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (bio.isNotBlank()) {
            Text(text = "(", color = Color.White)
            Text(
                text = bio,
                style = Typography.bodyMedium.copy(
                    color = GrayTextColor,
                    fontStyle = FontStyle.Italic
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = ")", color = Color.White)
        }
    }
}

@Composable
private fun Username(currentUser: UserEntityFirebase) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = currentUser.username,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.W900,
                color = Color.White
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 290.dp)
        )

        if (currentUser.verified) {
            Icon(
                modifier = Modifier.size(22.dp),
                painter = painterResource(R.drawable.verified),
                contentDescription = "Verified",
                tint = VerifiedColor
            )
        }
    }
}

@Composable
private fun SocializationButtons(user: UserEntityFirebase) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val currentUser by userViewModel.loggedUser

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        currentUser?.let {
            FollowButton(currentUser!!, user, userViewModel)
            SupportButton(currentUser!!, user, userViewModel)
        }
    }
}

@Composable
private fun FollowButton(
    currentUser: UserEntityFirebase,
    user: UserEntityFirebase,
    userViewModel: FirebaseUserViewModel,
) {
    val scope = rememberCoroutineScope()
    val notificationViewModel = hiltViewModel<NotificationViewModel>()

    val isFollowed = user.followerIds.any { it == currentUser.uid }
    val isFollowing = currentUser.followingIds.any { it == user.uid }

    Box(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .width(140.dp)
            .background(BrushPrimaryGradient, RoundedCornerShape(8.dp))
            .clickable {
                scope.launch {
                    userViewModel.updateUser(user.copy(followerIds = if (isFollowed) user.followerIds - currentUser.uid else user.followerIds + currentUser.uid))
                    userViewModel.updateUser(currentUser.copy(followingIds = if (isFollowing) currentUser.followingIds - user.uid else currentUser.followingIds + user.uid))

                    val userNotification =
                        notificationViewModel.getNotificationByEntityId(entityId = currentUser.uid)

                    val newNotification = if (userNotification == null) {
                        NotificationEntity(
                            userId = user.uid,
                            entityId = currentUser.uid,
                            users = listOf(currentUser.uid),
                            type = "FOLLOWED"
                        )
                    } else {
                        NotificationEntity(
                            userId = user.uid,
                            entityId = currentUser.uid,
                            users = listOf(currentUser.uid),
                            type = if (isFollowed) "UNFOLLOWED" else "FOLLOWED"
                        )
                    }

                    notificationViewModel.createNotification(newNotification)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(if (isFollowed) R.drawable.following else R.drawable.follow),
                contentDescription = "Follows",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = if (isFollowed) "Following" else "Follow",
                style = Typography.bodyLarge.copy(color = Color.White)
            )
        }
    }
}

@Composable
private fun SupportButton(
    currentUser: UserEntityFirebase,
    user: UserEntityFirebase,
    userViewModel: FirebaseUserViewModel,
) {
    val scope = rememberCoroutineScope()
    val notificationViewModel = hiltViewModel<NotificationViewModel>()

    val isSupported = user.supporterIds.any { it == currentUser.uid }

    Box(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .width(140.dp)
            .background(SecondaryDark, RoundedCornerShape(8.dp))
            .border(
                width = 0.5.dp,
                color = TertiaryDark,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                scope.launch {
                    userViewModel.updateUser(
                        user.copy(supporterIds = if (isSupported) user.supporterIds - currentUser.uid else user.supporterIds + currentUser.uid)
                    )

                    val userNotification =
                        notificationViewModel.getNotificationByEntityId(entityId = currentUser.uid)

                    val newNotification = if (userNotification == null) {
                        NotificationEntity(
                            userId = user.uid,
                            entityId = currentUser.uid,
                            users = listOf(currentUser.uid),
                            type = "SUPPORTED"
                        )
                    } else {
                        NotificationEntity(
                            userId = user.uid,
                            entityId = currentUser.uid,
                            users = listOf(currentUser.uid),
                            type = if (isSupported) "UNSUPPORTED" else "SUPPORTED"
                        )
                    }

                    notificationViewModel.createNotification(newNotification)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(if (isSupported) R.drawable.supporting else R.drawable.supporter),
                contentDescription = "Add",
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )

            Text(
                text = if (isSupported) "Supported" else "Support",
                style = Typography.bodyLarge.copy(color = Color.White)
            )
        }
    }
}

@Composable
private fun Interactions(
    postCount: Long,
    shareCount: Long,
    saveCount: Long,
    favoritesCount: Long,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Interaction(R.drawable.post, postCount, "Posts")
            Interaction(R.drawable.share, shareCount, "Shared")
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Interaction(R.drawable.save_post, saveCount, "Saved")
            Interaction(R.drawable.favorites, favoritesCount, "Favorites")
        }
    }
}

@Composable
fun Interaction(@DrawableRes iconRes: Int, count: Long, label: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = "Interaction",
            modifier = Modifier
                .size(18.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawGradient()
        )

        Text(
            text = "$count $label",
            style = MaterialTheme.typography.labelSmall.copy(color = GrayTextColor)
        )
    }
}

@Composable
fun CreateBio(bio: String) {
    Text(
        text = bio,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = Color.White,
            textAlign = TextAlign.Center
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    )
}

@Composable
private fun Follows(user: UserEntityFirebase) {
    val types = listOf("Followers" to user.followerIds.size, "Following" to user.followingIds.size)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        types.forEach { type ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${type.second}",
                    style = Typography.bodyMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                )

                Text(
                    text = type.first,
                    style = Typography.bodyMedium.copy(color = GrayTextColor)
                )
            }
        }
    }
}

@Composable
private fun Supports(user: UserEntityFirebase) {
    if (user.supporterIds.isNotEmpty()) {
        val userViewModel = hiltViewModel<FirebaseUserViewModel>()

        Column(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.supporter),
                    contentDescription = "Supporter",
                    modifier = Modifier
                        .size(18.dp)
                        .drawGradient(),
                )

                Text(
                    text = "${user.supporterIds.size}",
                    style = Typography.bodyMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                )

                Text(
                    text = if (user.supporterIds.size > 1) "Supporters" else "Supporter",
                    style = Typography.bodyMedium.copy(color = GrayTextColor)
                )
            }

            Row {
                user.supporterIds.take(if (user.supporterIds.size > 15) 15 else user.supporterIds.size)
                    .forEach { supporterId ->
                        var supporter by remember { mutableStateOf<UserEntityFirebase?>(null) }

                        LaunchedEffect(supporterId) {
                            supporter = userViewModel.getUserCached(supporterId)
                        }

                        supporter?.let {
                            AsyncImage(
                                model = it.avatarUrl,
                                contentDescription = it.username,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 0.5.dp,
                                        brush = BrushPrimaryGradient,
                                        shape = CircleShape
                                    ),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                if (user.supporterIds.size > 15) {
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        text = "+${user.supporterIds.size - 15}",
                        style = Typography.bodyMedium.copy(color = GrayTextColor)
                    )
                }
            }
        }
    }
}

@Composable
fun CreateJoinedDate(joinedDate: String) {
    Text(
        text = "Joined on $joinedDate",
        style = MaterialTheme.typography.bodySmall.copy(
            color = GrayTextColor,
            textAlign = TextAlign.Center,
            fontStyle = FontStyle.Italic
        ),
        modifier = Modifier.fillMaxWidth()
    )
}