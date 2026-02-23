package com.emc.moodmingle.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.viewmodel.dailymood.DailyMoodViewModel
import com.emc.moodmingle.ui.settings.saved.media.getMime
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.components.Avatar
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
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = "Daily Moods",
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            color = Color.White,
            fontWeight = FontWeight.Black
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(dailyMoods) { index, dailyMood ->
                val user by userViewModel.getUserByUid(dailyMood.userId)
                    .collectAsState(initial = null)
                val media = dailyMood.media
                val text = dailyMood.text

                Box(
                    modifier = Modifier
                        .padding(start = if (index == 0) 12.dp else 0.dp)
                        .height(200.dp)
                        .width(150.dp)
                        .background(PrimaryDark, RoundedCornerShape(16.dp))
                        .clickable {},
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = user?.getOrNull()?.avatarUrl.orEmpty(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        alpha = 0.3f,
                        colorFilter = ColorFilter.tint(
                            color = Color.Black,
                            blendMode = BlendMode.Plus
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    if (media.urls.isNotEmpty()) {
                        val url = media.urls[0]
                        val mimeType = getMime(url)

                        when {
                            mimeType.startsWith("image") -> {

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
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        Text(text = dailyMood.mood.emoji, color = Color.White, fontSize = 20.sp)
                    }

                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .align(Alignment.BottomCenter)
                            .border(width = 0.5.dp, color = Color.Red, shape = CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.love),
                            contentDescription = null,
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}