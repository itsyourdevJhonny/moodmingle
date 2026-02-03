package com.emc.moodmingle.ui.create.post.pickers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign

@Composable
fun BoxScope.CreatePostPickers(
    showFontPicker: Boolean,
    showColorPicker: Boolean,
    showAlignPicker: Boolean,
    selectedFont: FontFamily,
    selectedColor: Color,
    selectedAlign: TextAlign,
    onFontSelected: (FontFamily) -> Unit,
    onColorSelected: (Color) -> Unit,
    onAlignSelected: (TextAlign) -> Unit
) {
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .animateContentSize()
    ) {
        AnimatedVisibility(
            visible = showFontPicker || showColorPicker || showAlignPicker,
            enter = fadeIn(),
            exit = fadeOut()
        ) {

            when {
                showFontPicker -> {
                    CreatePostFontPicker(selectedFont, onFontSelected)
                }

                showColorPicker -> {
                    CreatePostColorPicker(selectedColor, onColorSelected)
                }

                showAlignPicker -> {
                    CreatePostAlignPicker(selectedAlign, onAlignSelected)
                }
            }
        }
    }
}