package com.emc.moodmingle.ui.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.UserEntityFirebase
import com.emc.moodmingle.ui.profile.utils.ShowProfilePicture
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.VerifiedColor
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun ProfileSection(
    loggedUser: UserEntityFirebase?,
    postCount: Long,
    shareCount: Long,
    saveCount: Long,
    favoritesCount: Long
) {
    val joinedDate = loggedUser?.joinedDate ?: ""

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            loggedUser?.let {
                CreateAvatar(
                    loggedUser,
                    postCount,
                    shareCount,
                    saveCount,
                    favoritesCount
                )

                CreateUsername(currentUser = loggedUser)
            }

            CreateJoinedDate(joinedDate)
        }
    }
}

@Composable
private fun CreateAvatar(
    loggedUser: UserEntityFirebase,
    postCount: Long,
    shareCount: Long,
    saveCount: Long,
    favoritesCount: Long
) {
    var showProfilePicture by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = loggedUser.avatarUrl,
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.3f))
                .border(
                    width = 1.dp,
                    brush = BrushPrimaryGradient,
                    shape = CircleShape
                )
                .clickable { showProfilePicture = true }
        )

        Column {
            Interactions(
                postCount = postCount,
                shareCount = shareCount,
                saveCount = saveCount,
                favoritesCount = favoritesCount
            )

            Bio(loggedUser.bio)
        }
    }

    if (showProfilePicture) {
        ShowProfilePicture(loggedUser.avatarUrl) {
            showProfilePicture = false
        }
    }
}

@Composable
private fun Bio(bio: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (bio.isNotBlank()) {
            Text(text = "(", color = Color.White)
            Text(
                text = bio,
                style = MaterialTheme.typography.bodyMedium.copy(
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
fun CreateUsername(currentUser: UserEntityFirebase) {
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
private fun Interactions(postCount: Long, shareCount: Long, saveCount: Long, favoritesCount: Long) {
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