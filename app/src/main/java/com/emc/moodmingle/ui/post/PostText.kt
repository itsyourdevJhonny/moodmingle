package com.emc.moodmingle.ui.post

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PostText(content: String) {
    Text(
        modifier = Modifier.padding(bottom = 12.dp, start = 16.dp, end = 16.dp).fillMaxWidth(),
        text = content,
        style = MaterialTheme.typography.bodySmall.copy(
            color = Color.White,
            textAlign = TextAlign.Justify
        )
    )
}