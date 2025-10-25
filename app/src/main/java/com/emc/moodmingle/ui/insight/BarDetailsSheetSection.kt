package com.emc.moodmingle.ui.insight

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.ui.screens.InsightData

@Composable
fun BarDetailsSheet(selectedLabel: String, data: InsightData) {
    val detailText = when (selectedLabel) {
        "Posts" -> "You've created ${data.posts} posts this period. Great consistency!"
        "Reacts" -> "You received ${data.reactions} reactions. People are engaging!"
        "Comments" -> "You got ${data.comments} comments. Keep interacting!"
        "Score" -> "Your average engagement score is ${data.avgScore}."
        else -> "No details available."
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = selectedLabel,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = detailText,
            textAlign = TextAlign.Center,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}