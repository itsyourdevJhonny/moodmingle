package com.emc.moodmingle.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.remix.Mood
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.domain.local.model.post.formatTimeAgo
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel

@Composable
fun PostHeader(
    user: UserEntityFirebase?,
    tagUserIds: List<String>,
    primaryColor: Color,
    secondaryColor: Color,
    mood: Mood,
    location: String,
    onMore: (() -> Unit)? = null
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Avatar(user)

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                UsernameAndTag(user, tagUserIds, mood)
                Location(location)
                TimeAgo()
            }
        }

        MoreIconAndMood(mood, primaryColor, secondaryColor, onMore)
    }
}

@Composable
private fun Avatar(user: UserEntityFirebase?) {
    AsyncImage(
        model = user?.avatarUrl,
        contentDescription = "Avatar",
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .gradientCircleBorder(),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun UsernameAndTag(user: UserEntityFirebase?, tagUserIds: List<String>, mood: Mood) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()

    val usernames = tagUserIds.map { userId ->
        userViewModel.getUserById(userId).collectAsState(initial = null).value?.username ?: ""
    }

    Column {
        Text(
            text = buildAuthorTaggedAnnotatedText(
                authorUsername = user?.username.orEmpty(),
                usernames = usernames,
                maxVisible = 2
            ),
            modifier = Modifier.widthIn(
                max = when (mood.description.length) {
                    3 -> 232.dp
                    4 -> 226.dp
                    5 -> 218.dp
                    6 -> 216.dp
                    7 -> 210.dp
                    10 -> 192.dp
                    else -> 176.dp
                }
            )
        )
    }
}

@Composable
private fun Location(location: String) {
    if (location.isNotBlank()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.location),
                contentDescription = "Location",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )

            Text(
                text = location,
                style = Typography.bodySmall,
                modifier = Modifier.widthIn(max = 158.dp)
            )
        }
    }
}

@Composable
private fun TimeAgo() {
    Text(
        text = formatTimeAgo(System.currentTimeMillis() - 5000),
        style = Typography.bodySmall.copy(color = GrayTextColor)
    )
}

@Composable
private fun MoreIconAndMood(
    mood: Mood,
    primaryColor: Color,
    secondaryColor: Color,
    onMore: (() -> Unit)? = null
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(R.drawable.more),
            contentDescription = "More",
            tint = Color.White,
            modifier = Modifier.clickable(enabled = onMore != null) { onMore?.invoke() }
        )

        if (mood.description.isNotEmpty()) {
            MoodCard(mood, primaryColor, secondaryColor)
        }
    }
}

/**
 * Builds a styled author + tagged users text.
 *
 * behavior:
 * - always shows author username
 * - shows tagged users ONLY if usernames is not empty
 *
 * styling:
 * - author username: white, bold
 * - "tagged" and "and X others": gray
 * - mentioned usernames: white, bold
 */
fun buildAuthorTaggedAnnotatedText(
    authorUsername: String,
    usernames: List<String>,
    maxVisible: Int = 3
): AnnotatedString {
    return buildAnnotatedString {

        // author username (always visible)
        withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Black)) {
            append(authorUsername)
        }

        // stop here if no tagged users
        if (usernames.isEmpty()) return@buildAnnotatedString

        // " tagged "
        withStyle(SpanStyle(color = GrayTextColor, fontSize = 14.sp)) {
            append(" tagged ")
        }

        val visibleUsers = usernames.take(maxVisible)
        val remainingCount = usernames.size - visibleUsers.size

        // mentioned usernames
        withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Black)) {
            append(visibleUsers.joinToString(", "))
        }

        // " and X others"
        if (remainingCount > 0) {
            val othersLabel = if (remainingCount == 1) "1 other" else "$remainingCount others"

            withStyle(SpanStyle(color = GrayTextColor)) {
                append(", and $othersLabel")
            }
        }
    }
}

@Composable
fun MoodCard(mood: Mood, primaryColor: Color, secondaryColor: Color) {
    Box(modifier = Modifier.background(primaryColor, RoundedCornerShape(12.dp))) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = mood.emoji, style = Typography.bodyLarge.copy(color = Color.White))

            Text(
                text = mood.description,
                style = Typography.labelSmall.copy(
                    color = secondaryColor,
                    fontWeight = FontWeight.Black
                )
            )
        }
    }
}