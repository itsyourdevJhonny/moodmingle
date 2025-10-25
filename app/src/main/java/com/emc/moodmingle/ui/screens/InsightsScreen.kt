package com.emc.moodmingle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.ui.insight.MainContent
import com.emc.moodmingle.ui.insight.TimeRangeSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(onBackClick: () -> Unit) {
    var selectedPeriod by remember { mutableStateOf("Week") }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(modifier = Modifier.padding(top = 25.dp),
            text = "Insights",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 30.sp
        )

        TimeRangeSelector(
            selected = selectedPeriod,
            onSelected = { selectedPeriod = it },
            onBackClick = onBackClick
        )
        MainContent(selectedPeriod)
    }
}

data class InsightData(
    val posts: Int,
    val reactions: Int,
    val comments: Int,
    val avgScore: Double
)

@Preview(showBackground = true)
@Composable
fun InsightsPreview() {
    InsightsScreen(onBackClick = {})
}