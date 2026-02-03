package com.emc.moodmingle.ui.remix

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.font.FontOption
import com.emc.moodmingle.utils.font.FontUtils
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.modifier.grayCircleBorder
import com.emc.moodmingle.utils.modifier.roundedGrayBorder

@Composable
fun RemixFontPickerDialog(
    hashtag: String,
    caption: String,
    description: String,
    currentFont: FontFamily,
    fonts: List<FontOption> = FontUtils.getDefaultFonts(),
    onDismiss: () -> Unit,
    onFontSelected: (FontFamily) -> Unit
) {
    var selectedFont by remember { mutableStateOf(currentFont) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(0.7f), RoundedCornerShape(8.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Information(hashtag, caption, description, currentFont, selectedFont)
                SelectText()

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(fonts) { font ->
                        FontItem(
                            fontOption = font,
                            isSelected = font.fontFamily == selectedFont,
                            onClick = { selectedFont = font.fontFamily }
                        )
                    }
                }

                CancelAndOkayButton(onFontSelected, selectedFont, onDismiss)
            }
        }
    }
}

@Composable
private fun ColumnScope.CancelAndOkayButton(
    onFontSelected: (FontFamily) -> Unit,
    selectedFont: FontFamily,
    onDismiss: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.align(Alignment.End)
    ) {
        listOf("Cancel", "Okay").forEach { text ->
            TextButton(
                onClick = {
                    if (text == "Okay") onFontSelected(selectedFont)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = if (text == "Cancel") Color.Red else Color.White,
                    containerColor = Color.Transparent
                ),
                modifier = Modifier
                    .grayCircleBorder()
                    .widthIn(80.dp),
                content = { Text(text = text) }
            )
        }
    }
}

@Composable
private fun SelectText() {
    Text(
        text = "Select Font Style",
        style = Typography.titleMedium.copy(
            color = Color.White,
            fontWeight = FontWeight.Black
        )
    )
}

@Composable
private fun Information(
    hashtag: String,
    caption: String,
    description: String,
    currentFont: FontFamily,
    selectedFont: FontFamily
) {
    if (hashtag.isNotEmpty() || caption.isNotEmpty() || description.isNotEmpty()) {
        Box(
            modifier = Modifier
                .background(PrimaryDark, RoundedCornerShape(8.dp))
                .roundedGrayBorder(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .animateContentSize()
            ) {
                listOf(
                    hashtag to R.drawable.hashtag,
                    caption to R.drawable.caption,
                    description to R.drawable.description
                ).forEach { (text, icon) ->
                    if (text.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(icon),
                                contentDescription = "Icon",
                                modifier = Modifier
                                    .size(18.dp)
                                    .drawGradient()
                            )

                            Text(
                                text = text,
                                style = when (icon) {
                                    R.drawable.hashtag -> Typography.titleMedium.copy(fontWeight = FontWeight.Black)
                                    R.drawable.caption -> Typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                    else -> Typography.bodyMedium.copy(fontFamily = currentFont)
                                },
                                fontFamily = selectedFont,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FontItem(fontOption: FontOption, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) PurpleDark else Color.Transparent, RoundedCornerShape(8.dp))
            .roundedGrayBorder(8.dp)
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = fontOption.name,
            fontFamily = fontOption.fontFamily,
            style = MaterialTheme.typography.titleMedium.copy(if (isSelected) Color.White else Color.Unspecified)
        )
    }
}