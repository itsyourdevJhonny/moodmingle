package com.emc.moodmingle.ui.profile.storymood

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.TertiaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun StoryMood() {
    Box(
        modifier = Modifier
            .background(SecondaryDark, RoundedCornerShape(12.dp))
            .border(
                width = 0.5.dp,
                color = TertiaryDark,
                shape = RoundedCornerShape(12.dp)
            )
            .widthIn(max = 150.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "😀",
                    style = Typography.bodySmall.copy(color = Color.White),
                )

                Text(
                    text = "Happy",
                    style = Typography.bodySmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    ),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = "Needs support qghahajha",
                style = Typography.labelSmall.copy(color = Color.White),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.music_note),
                    contentDescription = "Music",
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )

                Text(
                    text = "Rob Deniel: Ikaw Sana",
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 114.dp)
                )
            }
        }

        Image(
            painter = painterResource(R.drawable.feelings_colored),
            contentDescription = "Feelings",
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.TopStart)
                .offset(x = (-8).dp, y = (-4).dp),
            contentScale = ContentScale.Crop
        )

        Icon(
            painter = painterResource(R.drawable.reply_right),
            contentDescription = "Reply",
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.TopEnd)
                .offset(x = 12.dp, y = (-4).dp)
                .graphicsLayer(alpha = 0.99f)
                .drawGradient()
        )
    }
}