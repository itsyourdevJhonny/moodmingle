package com.emc.moodmingle.ui.create.post.pickers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.emc.moodmingle.ui.remix.ColorHexCharacters
import com.emc.moodmingle.ui.remix.ColorPicker
import com.emc.moodmingle.ui.remix.ColorPreview

@Composable
fun CreatePostColorPicker(selectedColor: Color, onColorSelected: (Color) -> Unit) {
    var lightness by remember { mutableFloatStateOf(0.5f) }

    val adjustedColor = remember(selectedColor, lightness) {
        adjustColorLightness(selectedColor, lightness)
    }

    LaunchedEffect(adjustedColor) { onColorSelected(adjustedColor) }

    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        /* MAIN COLOR CONTROLS */
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ColorPreview(adjustedColor)
            ColorPicker { onColorSelected(it) }
            ColorHexCharacters(adjustedColor)
        }

        /* VERTICAL LIGHTNESS SLIDER */
        VerticalLightnessSlider(
            lightness = lightness,
            onLightnessChanged = { lightness = it },
            baseColor = selectedColor
        )
    }
}

@Composable
fun VerticalLightnessSlider(
    lightness: Float,
    onLightnessChanged: (Float) -> Unit,
    baseColor: Color
) {
    var sliderHeightPx by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .width(36.dp)
            .height(220.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        adjustColorLightness(baseColor, 1f),
                        adjustColorLightness(baseColor, 0f)
                    )
                )
            )
            .onSizeChanged { sliderHeightPx = it.height }
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, _ ->
                    if (sliderHeightPx == 0) return@detectVerticalDragGestures

                    val y = change.position.y
                    val newLightness = 1f - (y / sliderHeightPx.toFloat())

                    onLightnessChanged(newLightness.coerceIn(0f, 1f))
                }
            }
    ) {

        /* SLIDER THUMB */
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset { IntOffset(x = 0, y = ((1f - lightness) * sliderHeightPx).toInt()) }
                .size(24.dp)
                .background(Color.White, CircleShape)
                .border(2.dp, Color.Black, CircleShape)
        )
    }
}

fun adjustColorLightness(color: Color, lightness: Float): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(color.toArgb(), hsl)

    hsl[2] = lightness.coerceIn(0f, 1f)

    return Color(ColorUtils.HSLToColor(hsl))
}



