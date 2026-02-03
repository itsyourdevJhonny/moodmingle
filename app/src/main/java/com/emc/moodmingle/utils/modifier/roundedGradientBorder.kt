package com.emc.moodmingle.utils.modifier

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient

fun Modifier.roundedGradientBorder(size: Dp): Modifier {
    return border(width = 0.5.dp, brush = BrushPrimaryGradient, shape = RoundedCornerShape(size))
}