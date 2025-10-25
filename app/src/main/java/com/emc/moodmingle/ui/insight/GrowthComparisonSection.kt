package com.emc.moodmingle.ui.insight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.ui.screens.InsightData
import com.emc.moodmingle.ui.theme.GrayTextColor

@Composable
fun GrowthComparison(current: InsightData, previous: InsightData) {
    val percent = { new: Int, old: Int ->
        if (old == 0) 0 else ((new - old) * 100 / old)
    }

    val postsGrowth = percent(current.posts, previous.posts)
    val reactsGrowth = percent(current.reactions, previous.reactions)
    val commentsGrowth = percent(current.comments, previous.comments)

    Text(
        text = "Growth since last period:",
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = Color.White,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Column {
        GrowthRow("Posts", postsGrowth)
        GrowthRow("Reactions", reactsGrowth)
        GrowthRow("Comments", commentsGrowth)
    }
}

@Composable
fun GrowthRow(label: String, percent: Int) {
    val color = if (percent >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
    val arrow = if (percent >= 0) "↑" else "↓"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Medium, color = GrayTextColor)
        Text(
            text = "$arrow ${kotlin.math.abs(percent)}%",
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}