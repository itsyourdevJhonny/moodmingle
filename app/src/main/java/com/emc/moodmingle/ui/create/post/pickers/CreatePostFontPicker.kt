package com.emc.moodmingle.ui.create.post.pickers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.utils.font.FontOption
import com.emc.moodmingle.utils.font.FontUtils
import com.emc.moodmingle.utils.modifier.roundedGrayBorder

@Composable
fun CreatePostFontPicker(selectedFont: FontFamily, onFontSelected: (FontFamily) -> Unit) {
    val fonts = FontUtils.getDefaultFonts()

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(fonts) { font ->
            FontItem(
                fontOption = font,
                isSelected = font.fontFamily == selectedFont,
                onClick = { onFontSelected(font.fontFamily) }
            )
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