package com.emc.moodmingle.ui.create.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.normal.NormalPostEntity
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.BrushSecondaryDarkGradient
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.BackIcon
import com.emc.moodmingle.utils.modifier.gradientCircleBorder

@Composable
fun CreatePostHeader(
    currentUser: UserEntityFirebase?,
    post: NormalPostEntity,
    onSettingsOpened: (Boolean) -> Unit,
    onPost: () -> Unit,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(top = 38.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BackIconAndLabel(onBack)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PostButton(currentUser, post, onPost)
            SettingsIcon(onSettingsOpened)
        }
    }
}

@Composable
private fun BackIconAndLabel(onBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BackIcon(onClick = onBack)
        Text(text = "Create Post", style = Typography.titleMedium.copy(color = Color.White))
    }
}

@Composable
private fun PostButton(
    currentUser: UserEntityFirebase?,
    post: NormalPostEntity,
    onPost: () -> Unit
) {
    val enabled = remember(post) {
        (post.description.text.isNotBlank() || post.urls.isNotEmpty()) && post.mood.emoji.isNotBlank()
    }

    Box(
        modifier = Modifier
            .background(
                brush = if (enabled) BrushPrimaryGradient else BrushSecondaryDarkGradient,
                shape = CircleShape
            )
            .clickable(enabled = enabled) { onPost() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(4.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.post),
                contentDescription = "Post",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            AsyncImage(
                model = currentUser?.avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .gradientCircleBorder(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun SettingsIcon(onSettingsOpened: (Boolean) -> Unit) {
    Icon(
        imageVector = Icons.Default.Settings,
        contentDescription = "Settings",
        tint = Color.White,
        modifier = Modifier
            .size(28.dp)
            .clickable { onSettingsOpened(true) }
    )
}
