package com.emc.moodmingle.ui.post

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PostText(content: String) {
    Text(
        text = content,
        color = Color.White,
        modifier = Modifier.padding(16.dp)
    )
}