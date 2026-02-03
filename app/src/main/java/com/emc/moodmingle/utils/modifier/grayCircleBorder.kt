package com.emc.moodmingle.utils.modifier

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.theme.TertiaryDark

fun Modifier.grayCircleBorder(): Modifier {
    return border(
        width = 0.5.dp,
        color = TertiaryDark,
        shape = CircleShape
    )
}