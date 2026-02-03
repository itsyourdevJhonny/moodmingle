package com.emc.moodmingle.ui.create.post.settings.temporary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.data.firebase.model.post.settings.PostTemporary
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.SwitchButton

@Composable
fun TemporaryDialogContent(
    paddingValues: PaddingValues,
    temporary: PostTemporary,
    onTemporaryChanged: (PostTemporary) -> Unit
) {
    val isEnabled = temporary.enabled

    Column(modifier = Modifier.padding(paddingValues)) {
        SwitchButton(
            label = "Temporary ${if (isEnabled) "Enabled" else "Disabled"}",
            isChecked = isEnabled,
            onCheckedChange = {
                onTemporaryChanged(temporary.copy(enabled = it, savedToArchive = false))
            }
        )

        HorizontalDivider(thickness = 0.5.dp)

        AnimatedVisibility(visible = isEnabled) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "The post will expire after 24 hours.",
                    style = Typography.bodySmall.copy(color = GrayTextColor)
                )

                SwitchButton(
                    label = "Save to Archived after expiration",
                    isChecked = temporary.savedToArchive,
                    padding = 0.dp,
                    labelColor = GrayTextColor,
                    onCheckedChange = { onTemporaryChanged(temporary.copy(savedToArchive = it)) }
                )
            }
        }
    }
}