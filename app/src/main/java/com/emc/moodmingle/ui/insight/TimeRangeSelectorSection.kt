package com.emc.moodmingle.ui.insight

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.ui.theme.PrimaryGradient
import com.emc.moodmingle.utils.components.BackIcon

@Composable
fun TimeRangeSelector(selected: String, onSelected: (String) -> Unit, onBack: () -> Unit) {
    val options = listOf("Week", "Month", "3 Months")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp, top = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BackIcon(onClick = onBack)

        options.forEach { label ->
            val isSelected = label == selected
            val colors = if (isSelected) PrimaryGradient else listOf(Color.White, Color.White)

            Box(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .background(
                        brush = if (isSelected) Brush.linearGradient(colors = colors)
                        else Brush.linearGradient(colors = colors),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelected(label) }
                    .padding(vertical = 6.dp, horizontal = 14.dp)
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.White else Color.Black,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }
}