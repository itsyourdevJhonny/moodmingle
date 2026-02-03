package com.emc.moodmingle.ui.profile.storymood.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.TertiaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun StoryMoodPrivacy() {
    Box(
        modifier = Modifier
            .background(SecondaryDark, CircleShape)
            .border(
                width = 0.5.dp,
                color = TertiaryDark,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.privacy),
                contentDescription = "Privacy",
                modifier = Modifier
                    .size(16.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )
            Text(
                text = "Privacy",
                style = Typography.bodyMedium.copy(color = GrayTextColor)
            )
        }
    }
}