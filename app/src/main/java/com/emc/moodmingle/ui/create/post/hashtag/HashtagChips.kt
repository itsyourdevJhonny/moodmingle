package com.emc.moodmingle.ui.create.post.hashtag

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.ui.theme.HashtagTextColor

@Composable
fun HashtagChips(hashtags: List<String>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        hashtags.forEach { tag ->
            Surface(
                color = HashtagTextColor,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.animateContentSize()
            ) {
                Text(
                    text = "#$tag",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}