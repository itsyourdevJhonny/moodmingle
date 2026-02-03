package com.emc.moodmingle.ui.insight

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.viewmodel.local.InsightViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    selectedPeriod: String,
    insightViewModel: InsightViewModel,
    userId: String
) {
    var selectedBar by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    val insightData by insightViewModel.insightData.collectAsState()
    val previousData by insightViewModel.previousInsightData.collectAsState()

    LaunchedEffect(userId, selectedPeriod) {
        insightViewModel.loadInsights(userId, selectedPeriod)
        insightViewModel.loadPreviousInsights(userId, selectedPeriod)
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
                StatCard("Posts", insightData.posts.toString(), Color(0xFF6C63FF))
                StatCard("Reactions", insightData.reactions.toString(), Color(0xFFEC407A))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCard("Comments", insightData.comments.toString(), Color(0xFF42A5F5))
                StatCard(
                    "Avg Score",
                    String.format(Locale.US, "%.2f", insightData.avgScore),
                    Color(0xFF66BB6A)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            ActiveDaySummary(selectedPeriod)

            Spacer(modifier = Modifier.height(20.dp))

            GrowthComparison(insightData, previousData)

            Spacer(modifier = Modifier.height(20.dp))

            BarChart(
                values = listOf(
                    insightData.posts.toFloat(),
                    insightData.reactions.toFloat(),
                    insightData.comments.toFloat(),
                    insightData.avgScore.toFloat()
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
                BarDetailsSheet(selectedBar, insightData)
            }
        }
    }
}