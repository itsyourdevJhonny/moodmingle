package com.emc.moodmingle.ui.create.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.utils.components.BackIcon

@Composable
fun CreatePostDialogHeader(
    label: String = "",
    okayLabel: String = "Okay",
    enabled: Boolean = true,
    onOkay: (() -> Unit)? = null,
    onBack: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(top = if (onOkay == null) 16.dp else 0.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BackIcon(onClick = onBack)

            if (label.isNotBlank()) {
                Text(text = label, color = Color.White)
            }
        }

        if (onOkay != null) {
            TextButton(
                onClick = onOkay,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryDark,
                    contentColor = Color.White
                ),
                enabled = enabled,
                content = { Text(text = okayLabel, fontWeight = FontWeight.Bold) }
            )
        }
    }
}