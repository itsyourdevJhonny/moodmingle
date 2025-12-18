package com.emc.moodmingle.ui.chat.input

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.TertiaryDark

@Composable
fun ChatTextField(message: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    TextField(
        value = message,
        onValueChange = { onValueChange(it) },
        placeholder = { Text(text = "Enter message...") },
        modifier = modifier,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = SecondaryDark,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedPlaceholderColor = GrayTextColor,
            focusedIndicatorColor = Color.Transparent,
            focusedContainerColor = TertiaryDark
        ),
        shape = RoundedCornerShape(8.dp)
    )
}
