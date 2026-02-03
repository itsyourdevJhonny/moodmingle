package com.emc.moodmingle.ui.create.post.pickers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.utils.modifier.grayCircleBorder

@Composable
fun CreatePostAlignPicker(selectedAlign: TextAlign, onAlignSelected: (TextAlign) -> Unit) {
    val aligns = listOf(
        TextAlign.Unspecified to R.drawable.text_align_normal,
        TextAlign.Center to R.drawable.text_align_center,
        TextAlign.Start to R.drawable.text_align_start,
        TextAlign.End to R.drawable.text_align_end,
        TextAlign.Justify to R.drawable.text_align_justify
    )

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(aligns) { (align, icon) ->
            val isSelected = selectedAlign == align
            AlignItem(icon, isSelected, onClick = { onAlignSelected(align) })
        }
    }
}

@Composable
private fun AlignItem(icon: Int, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(if (isSelected) PurpleDark else Color.Transparent, CircleShape)
            .grayCircleBorder()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "Icon",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}