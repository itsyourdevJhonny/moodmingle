package com.emc.moodmingle.utils.modifier

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun Modifier.scaleOnPress(): Modifier {
    var isTapped by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isTapped) 0.97f else 1f,
        animationSpec = tween(120),
        label = "tap_scale"
    )

    return scale(scale)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isTapped = true
                    tryAwaitRelease()
                    isTapped = false
                }
            )
        }
}