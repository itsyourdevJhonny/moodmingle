package com.emc.moodmingle.ui.create.post.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.create.post.pickers.CreatePostPickers
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.BrushSecondaryTertiaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.components.BackIcon

@Composable
fun CreatePostTextDialog(
    currentText: String,
    currentFont: FontFamily,
    currentColor: Color,
    currentAlign: TextAlign,
    onTextChanged: (String) -> Unit,
    onFontSelected: (FontFamily) -> Unit,
    onColorSelected: (Color) -> Unit,
    onAlignSelected: (TextAlign) -> Unit,
    onDismiss: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    var showFontPicker by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showAlignPicker by remember { mutableStateOf(false) }

    var text by remember { mutableStateOf(currentText) }
    var selectedFont by remember { mutableStateOf(currentFont) }
    var selectedColor by remember { mutableStateOf(currentColor) }
    var selectedAlign by remember { mutableStateOf(currentAlign) }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TextDialogHeader(
                text,
                selectedFont,
                selectedColor,
                selectedAlign,
                onDismiss,
                onTextChanged,
                onFontSelected,
                onColorSelected,
                onAlignSelected
            )
        },
        bottomBar = {
            TextDialogFooter(
                onShowFontPicker = {
                    if (showColorPicker) {
                        showColorPicker = false
                    }

                    if (showAlignPicker) {
                        showAlignPicker = false
                    }

                    showFontPicker = !showFontPicker
                },
                onShowColorPicker = {
                    if (showFontPicker) {
                        showFontPicker = false
                    }

                    if (showAlignPicker) {
                        showAlignPicker = false
                    }

                    showColorPicker = !showColorPicker
                },
                onShowAlignPicker = {
                    if (showFontPicker) {
                        showFontPicker = false
                    }

                    if (showColorPicker) {
                        showColorPicker = false
                    }

                    showAlignPicker = !showAlignPicker
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                cursorBrush = SolidColor(Color.White),
                textStyle = LocalTextStyle.current.copy(
                    color = selectedColor,
                    fontSize = 20.sp,
                    textAlign = if (selectedAlign == TextAlign.Unspecified && text.isEmpty()) TextAlign.Center else selectedAlign,
                    fontFamily = selectedFont
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (text.isEmpty() && !isFocused) {
                            Text(text = "Tap to enter text...", fontSize = 16.sp, color = GrayTextColor)
                        }

                        innerTextField()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { isFocused = it.isFocused }

            )

            CreatePostPickers(
                showFontPicker,
                showColorPicker,
                showAlignPicker,
                selectedFont,
                selectedColor,
                selectedAlign,
                onFontSelected = { selectedFont = it },
                onColorSelected = { selectedColor = it },
                onAlignSelected = { selectedAlign = it }
            )
        }
    }
}

@Composable
private fun TextDialogHeader(
    text: String,
    selectedFont: FontFamily,
    selectedColor: Color,
    selectedAlign: TextAlign,
    onDismiss: () -> Unit,
    onTextChange: (String) -> Unit,
    onFontSelected: (FontFamily) -> Unit,
    onColorSelected: (Color) -> Unit,
    onAlignSelected: (TextAlign) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        BackIcon(onClick = onDismiss)

        TextButton(
            onClick = {
                onTextChange(text)
                onFontSelected(selectedFont)
                onColorSelected(selectedColor)
                onAlignSelected(selectedAlign)

                onDismiss()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryDark,
                contentColor = Color.White
            ),
            enabled = text.isNotBlank()
        ) {
            Text(text = "Okay", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TextDialogFooter(
    onShowFontPicker: () -> Unit,
    onShowColorPicker: () -> Unit,
    onShowAlignPicker: () -> Unit
) {
    val actionIcons = listOf(
        R.drawable.font_colored,
        R.drawable.color_wheel,
        R.drawable.align_colored
    )

    var selectedActionIcon by remember { mutableIntStateOf(0) }
    var isSelected by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 42.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.background(PrimaryDark, CircleShape)) {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                actionIcons.forEach { actionIcon ->
                    val isIcon = selectedActionIcon == actionIcon

                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .background(
                                if (isSelected && isIcon) BrushPrimaryGradient else BrushSecondaryTertiaryGradient,
                                CircleShape
                            )
                            .clickable {
                                isSelected = if (selectedActionIcon != actionIcon) {
                                    true
                                } else {
                                    !isSelected
                                }

                                selectedActionIcon = actionIcon

                                when (actionIcon) {
                                    R.drawable.font_colored -> onShowFontPicker()
                                    R.drawable.color_wheel -> onShowColorPicker()
                                    R.drawable.align_colored -> onShowAlignPicker()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(actionIcon),
                            contentDescription = "Action",
                            modifier = Modifier.size(if (actionIcon == R.drawable.color_wheel) 40.dp else 28.dp)
                        )
                    }
                }
            }
        }
    }
}