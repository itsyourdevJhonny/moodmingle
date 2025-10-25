package com.emc.moodmingle.ui.insight

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.ui.theme.GrayTextColor
import kotlin.math.max

@Composable
fun BarChart(values: List<Float>, labels: List<String>, onBarClick: (String) -> Unit) {
    val maxValue = max(values.maxOrNull() ?: 1f, 1f)
    val animatedValues = values.map { value ->
        animateFloatAsState(targetValue = value, animationSpec = tween(800)).value
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(horizontal = 20.dp)
    ) {
        val barWidth = size.width / (values.size * 2)
        val maxHeight = size.height - 30f

        animatedValues.forEachIndexed { index, value ->
            val barHeight = (value / maxValue) * maxHeight
            val x = index * (barWidth * 2) + barWidth / 2
            val y = size.height - barHeight

            val barColor = when (index) {
                0 -> Color(0xFF6C63FF)
                1 -> Color(0xFFEC407A)
                2 -> Color(0xFF42A5F5)
                else -> Color(0xFF66BB6A)
            }

            drawRoundRect(
                color = barColor,
                topLeft = androidx.compose.ui.geometry.Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(10f, 10f)
            )

            drawIntoCanvas { canvas ->
                val labelValue =
                    if (value % 1f == 0f) value.toInt().toString() else "%.1f".format(value)
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 30f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                canvas.nativeCanvas.drawText(labelValue, x + (barWidth / 2), y - 10f, paint)
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        labels.forEach { label ->
            Text(
                text = label,
                fontSize = 13.sp,
                color = GrayTextColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable { onBarClick(label) }
            )
        }
    }
}