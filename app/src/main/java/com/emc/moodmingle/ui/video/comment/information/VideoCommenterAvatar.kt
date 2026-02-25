package com.emc.moodmingle.ui.video.comment.information

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient

@Composable
fun VideoCommenterAvatar(commenter: UserEntityFirebase?, onUserClick: (String) -> Unit) {
    AsyncImage(
        model = commenter?.avatarUrl,
        contentDescription = "Avatar",
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .border(width = 0.5.dp, brush = BrushPrimaryGradient, shape = CircleShape)
            .clickable { onUserClick(commenter?.uid ?: "") },
        contentScale = ContentScale.Crop
    )
}