package com.emc.moodmingle.ui.chat.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.SecondaryDark
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun InfiniteScrollingRow(
    suggestions: List<String>,
    textFieldValue: String,
    onSuggestionSelected: (String) -> Unit
) {
    val scrollState = rememberLazyListState()
    val infiniteList = remember { suggestions + suggestions }
    var isUserScrolling by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var scrollJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(scrollState.isScrollInProgress) {
        isUserScrolling = scrollState.isScrollInProgress
    }

    LaunchedEffect(isUserScrolling, textFieldValue) {
        scrollJob?.cancel()
        if (!isUserScrolling && textFieldValue.isEmpty()) {
            scrollJob = scope.launch {
                while (!isUserScrolling) {
                    scrollState.animateScrollBy(2f, tween(20, easing = LinearEasing))
                    if (scrollState.firstVisibleItemIndex >= infiniteList.size / 2) {
                        scrollState.scrollToItem(0)
                    }
                }
            }
        }
    }

    LazyRow(
        state = scrollState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        items(infiniteList.size) { index ->
            val suggestion = infiniteList[index]
            Box(
                modifier = Modifier
                    .background(SecondaryDark, CircleShape)
                    .border(0.3.dp, BrushPrimaryGradient, CircleShape)
                    .clickable { onSuggestionSelected(suggestion) }
            ) {
                Text(
                    text = suggestion,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
