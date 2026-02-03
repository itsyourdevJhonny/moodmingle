package com.emc.moodmingle.ui.remix

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.modifier.grayCircleBorder
import com.emc.moodmingle.utils.modifier.roundedGrayBorder

@Composable
fun RemixTextAlignPickerDialog(
    hashtag: String,
    caption: String,
    description: String,
    currentAlign: TextAlign,
    onAlignSelected: (TextAlign) -> Unit,
    onDismiss: () -> Unit
) {
    val aligns = listOf(
        TextAlign.Unspecified to R.drawable.text_align_normal,
        TextAlign.Center to R.drawable.text_align_center,
        TextAlign.Start to R.drawable.text_align_start,
        TextAlign.End to R.drawable.text_align_end,
        TextAlign.Left to R.drawable.text_align_left,
        TextAlign.Right to R.drawable.text_align_right,
        TextAlign.Justify to R.drawable.text_align_justify
    )

    var selectedAlign by remember { mutableStateOf(currentAlign) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(0.7f), RoundedCornerShape(8.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Information(hashtag, caption, description, selectedAlign)

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Choose Alignment",
                    style = Typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                )

                HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(vertical = 16.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(aligns) { (align, icon) ->
                        val isSelected = selectedAlign == align
                        AlignItem(icon, isSelected, onClick = { selectedAlign = align })
                    }
                }

                HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(vertical = 16.dp))

                CancelAndOkayButton(onAlignSelected, selectedAlign, onDismiss)
            }
        }
    }
}

@Composable
private fun PreviewTitle() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(bottom = 4.dp)
            .fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(R.drawable.view),
            contentDescription = "Preview",
            modifier = Modifier
                .size(20.dp)
                .drawGradient()
        )

        Text(
            text = "Preview",
            style = Typography.bodyMedium.copy(color = GrayTextColor)
        )
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

@Composable
private fun Information(
    hashtag: String,
    caption: String,
    description: String,
    selectedAlign: TextAlign
) {
    if (hashtag.isNotEmpty() || caption.isNotEmpty() || description.isNotEmpty()) {
        PreviewTitle()

        Box(modifier = Modifier.roundedGrayBorder(8.dp)) {
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
                                    else -> Typography.bodyMedium
                                },
                                textAlign = selectedAlign,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (icon != R.drawable.description) {
                            HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.CancelAndOkayButton(
    onAlignSelected: (TextAlign) -> Unit,
    selectedAlign: TextAlign,
    onDismiss: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.align(Alignment.End)
    ) {
        listOf("Cancel", "Okay").forEach { text ->
            TextButton(
                onClick = {
                    if (text == "Okay") onAlignSelected(selectedAlign)
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