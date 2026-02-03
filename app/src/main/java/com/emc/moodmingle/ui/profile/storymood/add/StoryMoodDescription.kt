package com.emc.moodmingle.ui.profile.storymood.add

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.TertiaryDark
import com.emc.moodmingle.ui.theme.Typography

@Composable
fun StoryMoodDescription(onDescription: (String) -> Unit) {
    var value by remember { mutableStateOf("") }
    val maxLength = 100

    Column(horizontalAlignment = Alignment.End) {
        TextField(
            value = value,
            onValueChange = {
                if (it.length <= 100) {
                    value = it
                    onDescription(value)
                }
            },
            shape = RoundedCornerShape(8.dp),
            placeholder = { Text(text = "What do you want to say?") },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = SecondaryDark,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedTextColor = Color.White,
                focusedContainerColor = SecondaryDark,
                focusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .border(
                    width = 0.4.dp,
                    color = TertiaryDark,
                    shape = RoundedCornerShape(8.dp)
                )
        )

        Text(
            text = "${value.length}/$maxLength",
            style = Typography.bodySmall.copy(color = if (value.length == 100) Color.Red else GrayTextColor)
        )
    }
}