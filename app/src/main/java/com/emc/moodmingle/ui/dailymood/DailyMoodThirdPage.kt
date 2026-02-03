package com.emc.moodmingle.ui.dailymood

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.ui.create.post.CreatePostDialogHeader
import com.emc.moodmingle.ui.create.post.hashtag.extractHashtags
import com.emc.moodmingle.ui.dailymood.dialog.TextSection
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.ExpandableAnnotatedText
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@Composable
fun DailyMoodThirdPage(
    dailyMood: DailyMoodEntity,
    onMood: (Boolean) -> Unit,
    onText: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    Scaffold(
        containerColor = Color.Black,
        topBar = { CreatePostDialogHeader(onBack = onBack) },
        floatingActionButton = { Actions(dailyMood, onMood, onText) }
    ) { paddingValues ->
        Content(paddingValues, dailyMood)
    }
}

@Composable
private fun Actions(dailyMood: DailyMoodEntity, onMood: (Boolean) -> Unit, onText: () -> Unit) {
    val actions = getActions()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        actions.forEach { (label, icon) ->
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(PrimaryDark, CircleShape)
                    .clickable {
                        when (label) {
                            "mood" -> onMood(true)
                            "text" -> onText()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                when {
                    label == "mood" && dailyMood.mood.description.isNotBlank() -> {
                        Text(text = dailyMood.mood.emoji, fontSize = 28.sp, color = Color.White)
                    }

                    label == "music" && dailyMood.musicTrack != null -> {
                        AsyncImage(
                            model = dailyMood.musicTrack.streamUrl,
                            contentDescription = "Music",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                        )
                    }

                    else -> {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = "Action",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Content(paddingValues: PaddingValues, dailyMood: DailyMoodEntity) {
    val dailyMoodText = dailyMood.text

    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (dailyMoodText.mentions.isNotEmpty()) {
                val userViewModel = hiltViewModel<FirebaseUserViewModel>()

                val mentionUsernames = dailyMoodText.mentions.map { userId ->
                    val user by remember(userId) {
                        userViewModel.getUserById(userId)
                    }.collectAsState(initial = null)

                    user?.username ?: ""
                }

                ExpandableAnnotatedText(
                    fullText = mentionUsernames.joinToString(", ") { "@$it" },
                    minLines = 1
                )
            }

            Text(text = dailyMoodText.text)

            if (dailyMoodText.hashtag.isNotBlank()) {
                val hashtags = extractHashtags(dailyMoodText.hashtag)

                ExpandableAnnotatedText(
                    fullText = hashtags.joinToString(" ") { "#${it.replace(" ", "")}" },
                    style = Typography.bodyLarge.copy(lineHeight = 20.sp),
                    minLines = 1
                )
            }
        }
    }
}

private fun getActions(): List<Pair<String, Int>> {
    return listOf(
        "mood" to R.drawable.mood,
        "text" to R.drawable.text_style,
        "media" to R.drawable.image_video,
        "music" to R.drawable.music_note,
        "location" to R.drawable.location
    )
}