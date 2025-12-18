package com.emc.moodmingle.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark

@Composable
fun DeletedMessageContent() {
    Box(
        modifier = Modifier
            .background(SecondaryDark, RoundedCornerShape(8.dp))
            .drawBehind {
                val strokeWidth = 0.5.dp.toPx()
                val dashLength = 10.dp
                val gapLength = 5.dp
                val cornerRadius = 8.dp.toPx()

                // create a dashed path effect
                val pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(dashLength.toPx(), gapLength.toPx()),
                    0f
                )

                drawRoundRect(
                    color = GrayTextColor,
                    topLeft = Offset(0f, 0f),
                    size = size,
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                    style = Stroke(width = strokeWidth, pathEffect = pathEffect)
                )
            }
    ) {
        Text(
            text = "Message deleted",
            color = GrayTextColor,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
        )
    }
}