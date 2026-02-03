package com.emc.moodmingle.utils.modifier

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.theme.TertiaryDark

fun Modifier.roundedGrayBorder(size: Dp): Modifier {
    return border(
        width = 0.5.dp,
        color = TertiaryDark,
        shape = RoundedCornerShape(size)
    )
}