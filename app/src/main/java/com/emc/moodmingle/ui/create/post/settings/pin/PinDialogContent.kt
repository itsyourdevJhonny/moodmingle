package com.emc.moodmingle.ui.create.post.settings.pin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.SwitchButton

@Composable
fun PinDialogContent(
    isPinned: Boolean,
    paddingValues: PaddingValues,
    onPinChanged: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "When post is pinned, it will be displayed initially in your profile feed.",
            style = Typography.bodySmall.copy(color = GrayTextColor)
        )

        HorizontalDivider(thickness = 0.5.dp)

        SwitchButton(
            label = if (isPinned) "Unpin" else "Pin",
            isChecked = isPinned,
            onCheckedChange = onPinChanged
        )
    }
}