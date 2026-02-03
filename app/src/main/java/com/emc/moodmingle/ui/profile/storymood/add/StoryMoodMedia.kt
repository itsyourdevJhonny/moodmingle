package com.emc.moodmingle.ui.profile.storymood.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.TertiaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun StoryMoodMedia() {
    var isPlayed by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(SecondaryDark, RoundedCornerShape(8.dp))
                .border(
                    width = 0.5.dp,
                    color = TertiaryDark,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.music_note),
                contentDescription = "Media Icon",
                modifier = Modifier
                    .size(32.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.width(235.dp)
            ) {
                Text(
                    text = "Rob Deniel",
                    style = Typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Ikaw Sana",
                    style = Typography.bodyMedium.copy(color = GrayTextColor),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                painter = painterResource(if (isPlayed) R.drawable.pause else R.drawable.play),
                contentDescription = "Play",
                modifier = Modifier
                    .size(28.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
                    .clickable { isPlayed = !isPlayed }
            )
        }
    }
}