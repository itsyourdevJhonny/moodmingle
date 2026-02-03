package com.emc.moodmingle.ui.create.post.settings.sensitive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.create.post.CreatePostDialogHeader
import com.emc.moodmingle.ui.post.action.toastMessage
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.SwitchButton

@Composable
fun SensitiveContentFlagDialog(
    isSensitiveFlagEnabled: Boolean,
    onSensitiveFlagChanged: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val original = remember(Unit) { isSensitiveFlagEnabled }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            CreatePostDialogHeader(
                label = "Enable Sensitive Content Flag",
                onBack = {
                    if (isSensitiveFlagEnabled != original) {
                        toastMessage(context, "Settings Saved")
                    }
                    onDismiss()
                }
            )
        }
    ) { paddingValues ->
        SensitiveContentFlagContent(paddingValues, isSensitiveFlagEnabled, onSensitiveFlagChanged)
    }
}

@Composable
fun SensitiveContentFlagContent(
    paddingValues: PaddingValues,
    isSensitiveFlagEnabled: Boolean,
    onSensitiveFlagChanged: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Enabling Sensitive Content Flag will strictly observe the comments on your post. If something sensitive content found, the comment could be warned or removed.",
            style = Typography.bodyMedium.copy(color = GrayTextColor, textAlign = TextAlign.Center)
        )

        HorizontalDivider(thickness = 0.5.dp)

        SwitchButton(
            label = if (isSensitiveFlagEnabled) "Enabled" else "Disabled",
            isChecked = isSensitiveFlagEnabled,
            padding = 0.5.dp,
            onCheckedChange = onSensitiveFlagChanged
        )
    }
}