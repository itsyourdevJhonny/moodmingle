package com.emc.moodmingle.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark

@Composable
fun SwitchButton(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)

        Box(
            modifier = Modifier
                .width(46.dp)
                .height(24.dp)
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = GrayTextColor,
                    shape = CircleShape
                )
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (isChecked) listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
                        else listOf(Color.Gray, Color.LightGray)
                    )
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier
                    .width(50.dp)
                    .height(24.dp)
                    .align(
                        if (isChecked) Alignment.CenterEnd else Alignment.CenterStart
                    ),
                thumbContent = {
                    Canvas(modifier = Modifier.size(18.dp)) {
                        drawCircle(
                            brush = if (isChecked) Brush.linearGradient(
                                listOf(
                                    Color.White,
                                    Color.White
                                )
                            ) else BrushPrimaryGradient,
                            radius = size.minDimension / 2
                        )
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Transparent,
                    uncheckedThumbColor = Color.Transparent,
                    checkedTrackColor = Color.Transparent,
                    uncheckedTrackColor = PrimaryDark,
                    uncheckedBorderColor = GrayTextColor
                )
            )
        }
    }
}