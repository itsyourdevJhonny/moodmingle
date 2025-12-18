package com.emc.moodmingle.utils.modifier

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient

@Composable
fun Modifier.drawGradient(): Modifier {
    return drawWithCache {
        onDrawWithContent {
            drawContent()
            drawRect(
                brush = BrushPrimaryGradient,
                blendMode = BlendMode.SrcAtop
            )
        }
    }
}