package com.emc.moodmingle.ui.dailymood

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.create.post.CreatePostDialogHeader
import com.emc.moodmingle.ui.theme.PrimaryDark

@Composable
fun DailyMoodSecondPage(
    onNextPage: () -> Unit,
    onShowMoodDialog: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            CreatePostDialogHeader(
                okayLabel = "Change Mood",
                onOkay = { onShowMoodDialog(true) },
                onBack = onBack
            )
        },
        floatingActionButton = { SkipButton(onNextPage) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

        }
    }
}

@Composable
private fun SkipButton(onNextPage: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier
                .clickable { onNextPage() }
                .background(PrimaryDark, CircleShape)
                .padding(8.dp)
        ) {

            Text(text = " Skip ", color = Color.White, fontWeight = FontWeight.Bold)

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Skip",
                tint = Color.White
            )
        }
    }
}