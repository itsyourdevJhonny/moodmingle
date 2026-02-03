package com.emc.moodmingle.utils.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.utils.modifier.roundedGradientBorder

@Composable
fun DiscardDialog(
    headerIcon: Any = R.drawable.discard,
    title: String,
    text: String,
    confirmText: String = "Discard",
    confirmIcon: Any = Icons.Default.Add,
    cancelText: String = "Okay",
    cancelIcon: Any = Icons.Default.Delete,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        containerColor = PrimaryDark,
        iconContentColor = Color.Red,
        titleContentColor = Color.White,
        textContentColor = GrayTextColor,
        shape = RoundedCornerShape(24.dp),
        icon = {
            Box(modifier = Modifier.size(38.dp)) {
                if (headerIcon is Int) {
                    Icon(
                        painter = painterResource(headerIcon),
                        contentDescription = text,
                        tint = Color.Red
                    )
                } else {
                    Icon(
                        imageVector = headerIcon as ImageVector,
                        contentDescription = text,
                        tint = Color.Red
                    )
                }
            }
        },
        title = { Text(text = title) },
        text = { Text(text = text, textAlign = TextAlign.Center) },
        confirmButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(
                    cancelText to cancelIcon,
                    confirmText to confirmIcon
                ).forEachIndexed { index, (text, icon) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clickable { if (index == 0) onCancel() else onConfirm() }
                            .background(
                                color = if (index == 0) SecondaryDark else Color.Red.copy(alpha = 0.7f),
                                shape = CircleShape
                            )
                            .width(140.dp)
                            .padding(vertical = 12.dp)
                    ) {
                        Box(modifier = Modifier.size(24.dp)) {
                            if (icon is Int) {
                                Icon(
                                    painter = painterResource(icon),
                                    contentDescription = text,
                                    tint = Color.White
                                )
                            } else {
                                Icon(
                                    imageVector = icon as ImageVector,
                                    contentDescription = text,
                                    tint = Color.White
                                )
                            }
                        }

                        Text(text = " $text", color = Color.White)
                    }
                }
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .roundedGradientBorder(24.dp)
    )
}