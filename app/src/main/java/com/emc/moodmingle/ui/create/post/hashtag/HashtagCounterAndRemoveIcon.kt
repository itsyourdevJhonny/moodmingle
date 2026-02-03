package com.emc.moodmingle.ui.create.post.hashtag

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography

@Composable
fun ColumnScope.HashtagCounterAndRemoveIcon(
    tags: List<String>,
    onHashtagChange: (TextFieldValue) -> Unit
) {
    AnimatedVisibility(visible = tags.isNotEmpty()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = "${tags.size} hashtag${if (tags.size > 1) "s" else ""}",
                style = Typography.bodyMedium.copy(color = GrayTextColor)
            )

            Icon(
                painter = painterResource(R.drawable.remove),
                contentDescription = "Remove",
                tint = Color.Red,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onHashtagChange(TextFieldValue("")) }
            )
        }
    }
}