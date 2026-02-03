package com.emc.moodmingle.ui.video.comment.more.secondary.trigger

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.utils.components.BackIcon

@Composable
fun TriggerPageHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BackIcon(onClick = onDismiss)
        Text(text = "Flag Comment As Triggering")
    }
}