package com.emc.moodmingle.ui.remix

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.emc.moodmingle.domain.remote.model.post.remix.Mood
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.theme.Typography

@Composable
fun RemixInspireBy(
    commenter: UserEntityFirebase?,
    color: Color,
    textColor: Color,
    mood: Mood,
    onClick: (String) -> Unit
) {
    Box(modifier = Modifier.padding(start = 8.dp)) {
        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            InspirerAvatarAndName(color, commenter, textColor, onClick)

            if (mood.description.isNotEmpty()) Mood(textColor, mood, color)
        }

        Circle(color)
    }
}

@Composable
private fun InspirerAvatarAndName(
    color: Color,
    commenter: UserEntityFirebase?,
    textColor: Color,
    onClick: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .background(color, CircleShape)
            .padding(8.dp)
            .clickable { onClick(commenter?.uid ?: "") }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = commenter?.avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Text(
                text = "${commenter?.username}",
                style = Typography.bodyMedium.copy(
                    color = textColor,
                    fontWeight = FontWeight.Black
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 170.dp)
            )
        }
    }
}

@Composable
private fun BoxScope.Circle(color: Color) {
    Box(
        modifier = Modifier
            .background(color, CircleShape)
            .size(20.dp)
            .align(Alignment.BottomStart)
    )
}

@Composable
private fun Mood(textColor: Color, mood: Mood, color: Color) {
    Box(modifier = Modifier.background(textColor, RoundedCornerShape(12.dp))) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.background(color, CircleShape)
            ) {
                Text(
                    text = mood.emoji,
                    style = Typography.bodyLarge.copy(color = Color.White),
                    modifier = Modifier.padding(4.dp)
                )
            }

            Text(
                text = mood.description,
                style = Typography.labelSmall.copy(color = color, fontWeight = FontWeight.Black)
            )
        }
    }
}