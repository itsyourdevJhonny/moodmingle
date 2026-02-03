package com.emc.moodmingle.ui.dailymood

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.create.post.CreatePostDialogHeader
import com.emc.moodmingle.ui.theme.SecondaryDark

@Composable
fun DailyMoodFirstPage(onShowMoodDialog: (Boolean) -> Unit, onBack: () -> Unit) {

    BackHandler { onBack() }

    Scaffold(
        containerColor = Color.Black,
        topBar = { CreatePostDialogHeader(onBack = onBack) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clickable { onShowMoodDialog(true) }
                        .background(SecondaryDark, CircleShape)
                        .padding(20.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.feelings_colored),
                        contentDescription = "Mood",
                        modifier = Modifier.size(40.dp)
                    )
                }

                Text(text = "Choose Mood", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}