package com.emc.moodmingle.ui.remix

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.emc.moodmingle.utils.color.toHex
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun RemixColorPickerDialog(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ColorPreview(selectedColor)
            ColorPicker(onColorSelected)
            ColorHexCharacters(selectedColor)
        }
    }
}

@Composable
fun ColorHexCharacters(selectedColor: Color) {
    Text(text = selectedColor.toHex(), fontWeight = FontWeight.Bold)
}

@Composable
fun ColorPicker(onColorSelected: (Color) -> Unit) {
    val controller = rememberColorPickerController()

    HsvColorPicker(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        controller = controller,
        onColorChanged = { colorEnvelope ->
            onColorSelected(colorEnvelope.color)
        }
    )
}

@Composable
fun ColorPreview(selectedColor: Color) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .background(selectedColor, shape = CircleShape)
    )
}