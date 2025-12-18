package com.emc.moodmingle.ui.settings.password.utils

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient

@Composable
fun TopIcon(imageVector: ImageVector, size: Dp) {
    Icon(
        imageVector = imageVector,
        contentDescription = "Password",
        modifier = Modifier
            .size(size)
            .graphicsLayer(alpha = 0.99f)
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    drawRect(
                        brush = BrushPrimaryGradient,
                        blendMode = BlendMode.SrcAtop
                    )
                }
            }
    )
}