package com.emc.moodmingle.ui.remix

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.remix.RemixEntity
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.domain.remote.viewmodel.post.remix.RemixViewModel
import com.emc.moodmingle.domain.local.model.post.formatTimeAgo
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.utils.text.NumberFormatter
import com.emc.moodmingle.utils.text.toColor
import com.emc.moodmingle.utils.text.toFontFamily
import com.emc.moodmingle.utils.text.toTextAlign
import com.emc.moodmingle.viewmodel.remote.CombinedPost
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel

@Composable
fun RemixItem(
    combinedPost: CombinedPost,
    remixViewModel: RemixViewModel,
    userViewModel: FirebaseUserViewModel,
    onClick: (String) -> Unit,
    onRemix: (String, String) -> Unit,
    onSelectedRemix: (RemixEntity?) -> Unit,
    onShowComment: (Boolean) -> Unit
) {
    val remix by remember { mutableStateOf(combinedPost.entity as RemixEntity) }

    val currentUser by userViewModel.loggedUser

    val user by remember(remix.userId) {
        userViewModel.getUserById(remix.userId)
    }.collectAsState(initial = null)

    val commenter by remember(remix.inspirerId) {
        userViewModel.getUserById(remix.inspirerId)
    }.collectAsState(initial = null)

    val color = remix.color.toColor()
    val textColor = if (color.luminance() < 0.5f) Color.White else Color.Black

    val fontStyle = remix.fontStyle.toFontFamily()
    val textAlign = remix.textAlignment.toTextAlign()

    val mood = remix.mood
    val hashtag = remix.hashtag
    val caption = remix.caption
    val description = remix.description

    Column(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .background(color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            RemixInspireBy(commenter, color, textColor, mood, onClick)

            Box(modifier = Modifier.background(color, RoundedCornerShape(16.dp))) {
                Column(modifier = Modifier.padding(8.dp)) {
                    UserAvatarAndUsername(user, color, textColor, combinedPost.createdAt)
                    RemixContentCard(
                        hashtag,
                        caption,
                        description,
                        textColor,
                        fontStyle,
                        textAlign
                    )
                }
            }
        }

        Actions(
            color,
            remix,
            currentUser,
            remixViewModel,
            onRemix,
            onSelectedRemix,
            onShowComment
        )
    }
}

@Composable
private fun Actions(
    color: Color,
    remix: RemixEntity,
    currentUser: UserEntityFirebase?,
    remixViewModel: RemixViewModel,
    onRemix: (String, String) -> Unit,
    onSelectedRemix: (RemixEntity?) -> Unit,
    onShowComment: (Boolean) -> Unit
) {
    val currentUserId = currentUser?.uid ?: ""

    val isReacted = remix.reactorIds.contains(currentUserId)
    val isDisliked = remix.dislikerIds.contains(currentUserId)

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        listOf(
            R.drawable.love to remix.reactorIds.size,
            R.drawable.dislike to remix.dislikerIds.size,
            R.drawable.comment to remix.comments.size,
            R.drawable.remix to remix.remixes.size
        ).forEach { (icon, count) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(color.copy(alpha = 0.3f), CircleShape)
                        .clickable {
                            var updatedRemix: RemixEntity? = null

                            when (icon) {
                                R.drawable.love -> {
                                    updatedRemix =
                                        remix.copy(reactorIds = if (isReacted) remix.reactorIds - currentUserId else remix.reactorIds + currentUserId)
                                }

                                R.drawable.dislike -> {
                                    updatedRemix =
                                        remix.copy(dislikerIds = if (isDisliked) remix.dislikerIds - currentUserId else remix.dislikerIds + currentUserId)
                                }

                                R.drawable.comment -> {
                                    onShowComment(true)
                                    onSelectedRemix(remix)
                                }

                                R.drawable.remix -> onRemix(remix.id, "POST")
                            }

                            updatedRemix?.let { remixViewModel.updateRemix(it) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = "Action",
                        tint = when (icon) {
                            R.drawable.love -> if (isReacted) Color.Red else Color.White
                            R.drawable.dislike -> if (isDisliked) PurpleDark else Color.White
                            else -> Color.White
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (count != 0) {
                    Text(
                        text = NumberFormatter.formatValue(count.toLong(), false),
                        style = Typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun UserAvatarAndUsername(
    user: UserEntityFirebase?,
    color: Color,
    textColor: Color,
    timestamp: Long
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = user?.avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .gradientCircleBorder(),
                contentScale = ContentScale.Crop
            )

            Column {
                Text(
                    text = user?.username ?: "",
                    style = Typography.bodyLarge.copy(
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = formatTimeAgo(timestamp),
                    style = Typography.bodySmall.copy(color = textColor)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(R.drawable.more),
                contentDescription = "More",
                tint = textColor
            )

            Box(modifier = Modifier.background(textColor, RoundedCornerShape(8.dp))) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.remix),
                        contentDescription = "Check",
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )

                    Text(
                        text = "Remixed",
                        style = Typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                    )
                }
            }
        }
    }
}