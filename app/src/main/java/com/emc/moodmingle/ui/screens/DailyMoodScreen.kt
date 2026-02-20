package com.emc.moodmingle.ui.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.domain.remote.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.domain.remote.viewmodel.dailymood.DailyMoodViewModel
import com.emc.moodmingle.service.post.DailyMoodService
import com.emc.moodmingle.ui.dailymood.page.DailyMoodFirstPage
import com.emc.moodmingle.ui.dailymood.page.DailyMoodSecondPage
import com.emc.moodmingle.ui.dailymood.page.DailyMoodThirdPage
import com.emc.moodmingle.ui.remix.MoodPickerDialog
import androidx.compose.runtime.collectAsState

@Composable
fun DailyMoodScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val dailyMoodViewModel = hiltViewModel<DailyMoodViewModel>()

    val dailyMoods by dailyMoodViewModel.allActiveDailyMoods.collectAsState()

    Log.d("DailyMoodScreen", "dailyMoods: $dailyMoods")

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
                onUpload = { performUploadOperation(context, mood, onBack) },
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

private fun performUploadOperation(context: Context, dailyMoodEntity: DailyMoodEntity, onBack: () -> Unit) {
    val uploadIntent = Intent(context, DailyMoodService::class.java).apply {
        action = DailyMoodService.ACTION_UPLOAD
        putExtra(DailyMoodService.EXTRA_DAILY_MOOD, dailyMoodEntity)
    }

    context.startForegroundService(uploadIntent)
    Toast.makeText(context, "Uploading post...", Toast.LENGTH_SHORT).show()
    onBack()
}