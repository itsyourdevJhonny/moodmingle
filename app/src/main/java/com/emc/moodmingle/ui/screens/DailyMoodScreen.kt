package com.emc.moodmingle.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.ui.dailymood.DailyMoodFirstPage
import com.emc.moodmingle.ui.dailymood.DailyMoodSecondPage
import com.emc.moodmingle.ui.dailymood.DailyMoodThirdPage
import com.emc.moodmingle.ui.dailymood.dialog.DailyMoodTextDialog
import com.emc.moodmingle.ui.remix.MoodPickerDialog

@Composable
fun DailyMoodScreen(onBack: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(3) }

    var showMoodDialog by remember { mutableStateOf(false) }
    var showTextDialog by remember { mutableStateOf(false) }

    var dailyMood by remember { mutableStateOf(DailyMoodEntity()) }

    when (currentPage) {
        1 -> DailyMoodFirstPage(onShowMoodDialog = { showMoodDialog = it }, onBack)

        2 -> {
            DailyMoodSecondPage(
                onNextPage = { currentPage++ },
                onShowMoodDialog = { showMoodDialog = it },
                onBack = { currentPage-- }
            )
        }

        3 -> {
            DailyMoodThirdPage(
                dailyMood,
                onMood = { showMoodDialog = it },
                onText = { showTextDialog = true },
                onBack = { currentPage-- }
            )
        }
    }

    when {
        showMoodDialog -> {
            MoodPickerDialog(
                selectedMood = dailyMood.mood,
                onSelectedMood = {
                    dailyMood = dailyMood.copy(mood = it)
                    if (currentPage != 2) currentPage++
                },
                onDismiss = { showMoodDialog = false }
            )
        }

        showTextDialog -> {
            DailyMoodTextDialog(
                onDismiss = { showTextDialog = false },
                onTextCreated = { dailyMood = dailyMood.copy(text = it) }
            )
        }
    }
}