package com.emc.moodmingle.utils.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.emc.moodmingle.ui.settings.password.utils.AnimatedText
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun LoadingDialog(text: String, onLoading: (Boolean) -> Unit = {}, performOperation: () -> Unit) {
    Dialog(
        onDismissRequest = { onLoading(false) },
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier
                    .size(58.dp)
                    .drawGradient(),
                strokeWidth = 4.dp,
            )

            AnimatedText("$text...")

            performOperation()
        }
    }
}