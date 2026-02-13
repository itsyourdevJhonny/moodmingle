package com.emc.moodmingle.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.ui.dailymood.page.DailyMoodFirstPage
import com.emc.moodmingle.ui.dailymood.page.DailyMoodSecondPage
import com.emc.moodmingle.ui.dailymood.page.DailyMoodThirdPage
import com.emc.moodmingle.ui.remix.MoodPickerDialog

@Composable
fun DailyMoodScreen(onBack: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(3) }
    var showMoodDialog by remember { mutableStateOf(false) }
    var mood by remember { mutableStateOf(DailyMoodEntity()) }

    when (currentPage) {
        1 -> DailyMoodFirstPage(onShowMoodDialog = { showMoodDialog = it }, onBack)

        2 -> {
            DailyMoodSecondPage(
                mood,
                onNextPage = { currentPage++ },
                onShowMoodDialog = { showMoodDialog = it },
                onMoodSelected = { emoji, description ->
                    mood = mood.copy(
                        mood = mood.mood.copy(
                            emoji = emoji,
                            description = getEmojiByEmotion().entries.firstOrNull { it.value == emoji }?.key.orEmpty()
                        )
                    )

                    mood = mood.copy(text = mood.text.copy(description = description))

                    currentPage++
                },
                onBack = { currentPage-- }
            )
        }

        3 -> {
            DailyMoodThirdPage(
                mood,
                onEdited = { mood = it },
                onTextPositionChanged = { mood = mood.copy(text = it) },
                onImagePositionChanged = { mood = mood.copy(media = mood.media.copy(image = it)) },
                onGifPositionChanged = { mood = mood.copy(gif = it) },
                onBack = { currentPage-- }
            )
        }
    }

    if (showMoodDialog) {
        MoodPickerDialog(
            selectedMood = mood.mood,
            onSelectedMood = {
                mood = mood.copy(mood = it)
                if (currentPage != 2) currentPage++
            },
            onDismiss = { showMoodDialog = false }
        )
    }
}