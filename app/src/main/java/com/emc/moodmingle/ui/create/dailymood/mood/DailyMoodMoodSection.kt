package com.emc.moodmingle.ui.create.dailymood.mood

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.domain.remote.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.theme.MentionTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel

@Composable
fun DailyMoodMoodSection(mood: DailyMoodEntity) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val currentUser by userViewModel.loggedUser

    AnimatedVisibility(
        visible = mood.mood.description.isNotBlank(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(horizontalAlignment = Alignment.End) {
            EmojiAndDescription(mood)
            CircleTails()
            CurrentUserAvatar(currentUser)
        }
    }
}

@Composable
private fun EmojiAndDescription(mood: DailyMoodEntity) {
    Box(
        modifier = Modifier
            .background(MentionTextColor, CircleShape)
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .animateContentSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = mood.mood.emoji, color = Color.White)

            Text(
                text = mood.mood.description,
                style = Typography.bodySmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )
            )
        }
    }
}

@Composable
private fun CircleTails() {
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        Column(horizontalAlignment = Alignment.End) {
            listOf(
                Triple(28.dp, 0.dp, (-8).dp),
                Triple(16.dp, (-8).dp, (-10).dp),
                Triple(8.dp, (-16).dp, (-12).dp),
            ).forEach { (size, offsetX, offsetY) ->
                Box(
                    modifier = Modifier
                        .size(size)
                        .offset(x = offsetX, y = offsetY)
                        .background(MentionTextColor, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun CurrentUserAvatar(currentUser: UserEntityFirebase?) {
    AsyncImage(
        model = currentUser?.avatarUrl.orEmpty(),
        contentDescription = "Avatar",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .offset(y = (-16).dp, x = (-16).dp)
            .size(52.dp)
            .clip(CircleShape)
            .gradientCircleBorder()
    )
}