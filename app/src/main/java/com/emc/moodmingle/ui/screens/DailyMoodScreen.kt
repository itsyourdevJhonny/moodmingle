package com.emc.moodmingle.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodText
import com.emc.moodmingle.ui.dailymood.DailyMoodFirstPage
import com.emc.moodmingle.ui.dailymood.DailyMoodSecondPage
import com.emc.moodmingle.ui.dailymood.DailyMoodThirdPage
import com.emc.moodmingle.ui.remix.MoodPickerDialog

@Composable
fun DailyMoodScreen(onBack: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(1) }

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
                onTextCreated = { dailyMood = dailyMood.copy(text = it) },
                onBack = { currentPage-- }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyMoodTextDialog(
    onDismiss: () -> Unit,
    onTextCreated: (DailyMoodText) -> Unit,
    onBack: () -> Int
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar() {

            }
        }
    ) { paddingValues ->
        TextDialogContent(paddingValues)
    }
}

@Composable
fun TextDialogContent(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column {

        }
    }
}