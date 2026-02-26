package com.emc.moodmingle.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.domain.remote.model.post.dailymood.media.DailyMoodMedia
import com.emc.moodmingle.domain.remote.model.post.dailymood.text.DailyMoodText
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.domain.remote.viewmodel.dailymood.DailyMoodViewModel
import com.emc.moodmingle.ui.settings.saved.media.getMime
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.components.Avatar
import com.emc.moodmingle.utils.modifier.roundedGrayBorder
import com.emc.moodmingle.utils.text.NumberFormatter
import com.emc.moodmingle.utils.text.toColor
import com.emc.moodmingle.utils.text.toFontFamily
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel

@Composable
fun DailyMoodSection() {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val dailyMoodViewModel = hiltViewModel<DailyMoodViewModel>()
    val dailyMoods by dailyMoodViewModel.allActiveDailyMoods.collectAsState()

    LaunchedEffect(Unit) {
        dailyMoodViewModel.observeAllActiveDailyMoods()
    }

    Column(
        modifier = Modifier
            .background(PrimaryDark)
            .padding(bottom = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Daily Moods",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            color = Color.White,
            fontWeight = FontWeight.Black
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(dailyMoods) { index, dailyMood ->
                val user by userViewModel.getUserByUid(dailyMood.userId)
                    .collectAsState(initial = null)
                val media = dailyMood.media
                val text = dailyMood.text

                DailyMoodItem(index, user, media, text, dailyMood, dailyMoodViewModel)
            }
        }
    }
}

@Composable
private fun DailyMoodItem(
    index: Int,
    user: Result<UserEntityFirebase>?,
    media: DailyMoodMedia,
    text: DailyMoodText,
    dailyMood: DailyMoodEntity,
    dailyMoodViewModel: DailyMoodViewModel,
) {
    val userDailyMoods by dailyMoodViewModel.activeDailyMoods.collectAsState()

    LaunchedEffect(Unit) {
        dailyMoodViewModel.observeActiveDailyMoods(user?.getOrNull()?.uid.orEmpty())
    }

    Box(
        modifier = Modifier
            .padding(start = if (index == 0) 12.dp else 0.dp)
            .height(200.dp)
            .width(150.dp)
            .background(PrimaryDark, RoundedCornerShape(16.dp))
            .clickable {},
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .roundedGrayBorder(16.dp)
        ) {
            AsyncImage(
                model = user?.getOrNull()?.avatarUrl.orEmpty(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(30.dp)
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.8f))
            )
        }

        if (media.urls.isNotEmpty()) {
            val url = media.urls[0]
            val mimeType = getMime(url)

            when {
                mimeType.startsWith("image") -> {
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
            }
        }

        if (text.description.isNotEmpty()) {
            Text(
                text = text.description,
                color = text.color.toColor(),
                fontFamily = text.font.toFontFamily()
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Avatar(model = user?.getOrNull()?.avatarUrl.orEmpty(), 38.dp)

            Text(
                text = user?.getOrNull()?.username.orEmpty(),
                style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    shadow = Shadow(color = Color.Black, offset = Offset(4f, 4f)),
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Text(text = dailyMood.mood.emoji, color = Color.White, fontSize = 20.sp)
        }

        BottomSection(userDailyMoods)
    }
}

@Composable
private fun BoxScope.BottomSection(userDailyMoods: List<DailyMoodEntity>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        IconButton(
            onClick = {},
            modifier = Modifier
                .border(width = 0.5.dp, color = Color.Red, shape = CircleShape)
                .size(38.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.love),
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = "87",
            style = TextStyle(
                color = Color.White,
                shadow = Shadow(color = Color.Black, offset = Offset(4f, 4f)),
            ),
            modifier = Modifier.weight(1f)
        )

        DailyMoodsCount(userDailyMoods)
    }
}

@Composable
private fun DailyMoodsCount(userDailyMoods: List<DailyMoodEntity>) {
    Text(
        text = NumberFormatter.formatValue(userDailyMoods.size.toLong(), true),
        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            shadow = Shadow(color = Color.Black, offset = Offset(4f, 4f)),
        )
    )
}