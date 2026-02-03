package com.emc.moodmingle.utils.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SuccessIcon() {
    Box(
        modifier = Modifier
            .background(Color.Green.copy(alpha = 0.7f), CircleShape)
            .padding(8.dp)
            .background(Color.Green.copy(alpha = 0.8f), CircleShape)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Check",
            tint = Color.White,
            modifier = Modifier.size(42.dp)
        )
    }
}