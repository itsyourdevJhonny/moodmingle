package com.emc.moodmingle.ui.video

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.normal.PostEntityFirebase
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.BrushSecondaryTertiaryGradient
import com.emc.moodmingle.ui.theme.TertiaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel

@Composable
fun VideoItemUserContent(post: PostEntityFirebase) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val user by remember(post.userId) {
        userViewModel.getUserByUid(post.userId)
    }.collectAsState(initial = null)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = post.avatarUrl,
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = post.username,
                    style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 195.dp)
                )

                if (user?.getOrNull()?.verified == true) {
                    Icon(
                        painter = painterResource(R.drawable.verified),
                        contentDescription = "Verified",
                        modifier = Modifier
                            .size(16.dp)
                            .drawGradient()
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .background(
                    BrushSecondaryTertiaryGradient,
                    CircleShape,
                    alpha = 0.6f
                )
                .border(
                    width = 0.5.dp,
                    color = TertiaryDark,
                    shape = CircleShape
                )
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                text = post.moodEmoji + " " + post.mood,
                color = Color.White,
                style = Typography.bodySmall
            )
        }
    }
}