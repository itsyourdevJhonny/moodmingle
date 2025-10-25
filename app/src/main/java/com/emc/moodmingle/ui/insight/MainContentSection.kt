package com.emc.moodmingle.ui.insight

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.screens.InsightData

@SuppressLint("UnusedCrossfadeTargetStateParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(selectedPeriod: String) {
    var selectedBar by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    val previousData = remember { InsightData(2, 8, 3, 3.0) }
    val currentData = when (selectedPeriod) {
        "Week" -> InsightData(3, 10, 4, 3.2)
        "Month" -> InsightData(10, 35, 12, 4.0)
        "3 Months" -> InsightData(24, 80, 30, 4.4)
        else -> InsightData(0, 0, 0, 0.0)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.Black)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCard("Posts", currentData.posts.toString(), Color(0xFF6C63FF))
                StatCard("Reactions", currentData.reactions.toString(), Color(0xFFEC407A))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCard("Comments", currentData.comments.toString(), Color(0xFF42A5F5))
                StatCard("Avg Score", currentData.avgScore.toString(), Color(0xFF66BB6A))
            }

            Spacer(modifier = Modifier.height(20.dp))
            ActiveDaySummary(selectedPeriod)

            Spacer(modifier = Modifier.height(20.dp))
            GrowthComparison(currentData, previousData)

            Spacer(modifier = Modifier.height(20.dp))

            BarChart(
                values = listOf(
                    currentData.posts.toFloat(),
                    currentData.reactions.toFloat(),
                    currentData.comments.toFloat(),
                    currentData.avgScore.toFloat()
                ),
                labels = listOf("Posts", "Reacts", "Comments", "Score"),
                onBarClick = { label ->
                    selectedBar = label
                    showSheet = true
                }
            )
            Spacer(modifier = Modifier.height(50.dp))
        }

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState
            ) {
                BarDetailsSheet(selectedBar, currentData)
            }
        }
    }
}