package com.emc.moodmingle.ui.users

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
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
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.domain.remote.viewmodel.dailymood.DailyMoodViewModel
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel
import kotlin.random.Random

@Composable
fun ActiveUserList(onCreate: () -> Unit) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val dailyMoodViewModel = hiltViewModel<DailyMoodViewModel>()

    val currentUser by userViewModel.loggedUser
    val allUsers by userViewModel.getAllUsers().collectAsState(initial = emptyList())

    val followerIds = currentUser?.followerIds.orEmpty()
    val followingIds = currentUser?.followingIds.orEmpty()
    val supporterIds = currentUser?.supporterIds.orEmpty()
    val allUserIds = allUsers.map { it.uid }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryDark)
    ) {
        Header(onCreate)

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = (followerIds + followingIds + supporterIds + allUserIds).distinct(),
                key = { it + it.hashCode() + Random.nextInt(0, 1000) }
            ) { userId ->
                val user by remember(userId) { userViewModel.getUserById(userId) }
                    .collectAsState(initial = null)

                UserItem(user, dailyMoodViewModel)
            }
        }
    }
}

@Composable
private fun Header(onCreate: () -> Unit) {
    HorizontalDivider(thickness = 0.5.dp)

    Spacer(Modifier.height(8.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.daily),
                contentDescription = "Daily",
                tint = Color.White,
                modifier = Modifier
                    .size(20.dp)
                    .drawGradient()
            )

            Text(text = "Daily Moods", style = Typography.titleMedium.copy(color = Color.White))
        }

        Box(
            modifier = Modifier
                .size(32.dp)
                .background(BrushPrimaryGradient, CircleShape)
                .clickable { onCreate() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun UserItem(user: UserEntityFirebase?, dailyMoodViewModel: DailyMoodViewModel) {
    val dailyMoods by dailyMoodViewModel.getDailyMoodsByUserId(user?.uid.orEmpty())
        .collectAsState(initial = emptyList())

    val hasDailyMood = dailyMoods.any { it.active }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {}
    ) {
        Box(contentAlignment = Alignment.Center) {
            ItemAvatar(user, hasDailyMood)
            ItemActiveIndicator(hasDailyMood)
        }

        ItemUsername(user)
    }
}

@Composable
private fun ItemUsername(user: UserEntityFirebase?) {
    Text(
        text = user?.username.orEmpty(),
        style = Typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Black),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.widthIn(max = 70.dp)
    )
}

@Composable
private fun BoxScope.ItemActiveIndicator(hasDailyMood: Boolean) {
    if (hasDailyMood) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .border(width = 3.dp, brush = BrushPrimaryGradient, CircleShape)
        )

        Box(
            modifier = Modifier
                .offset(x = 8.dp)
                .align(Alignment.TopEnd)
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Red, CircleShape)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "+87",
                    style = Typography.bodyMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun ItemAvatar(user: UserEntityFirebase?, hasDailyMood: Boolean) {
    AsyncImage(
        model = user?.avatarUrl,
        contentDescription = "Avatar",
        modifier = Modifier
            .size(if (hasDailyMood) 54.dp else 68.dp)
            .clip(CircleShape)
            .gradientCircleBorder(),
        contentScale = ContentScale.Crop
    )
}