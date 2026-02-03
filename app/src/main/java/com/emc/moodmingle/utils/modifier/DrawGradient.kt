package com.emc.moodmingle.utils.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient

fun Modifier.drawGradient(): Modifier {
    return graphicsLayer(alpha = 0.99f)
        .drawWithCache {
            onDrawWithContent {
                drawContent()
                drawRect(
                    brush = BrushPrimaryGradient,
                    blendMode = BlendMode.SrcAtop
                )
            }
        }
}