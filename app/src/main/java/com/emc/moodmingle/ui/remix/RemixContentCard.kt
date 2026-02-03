package com.emc.moodmingle.ui.remix

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.Typography
import kotlin.text.ifEmpty

@Composable
fun RemixContentCard(
    hashtag: String,
    caption: String,
    description: String,
    textColor: Color,
    fontStyle: FontFamily,
    textAlign: TextAlign
) {
    listOf(
        hashtag to R.drawable.hashtag,
        caption to R.drawable.caption,
        description to R.drawable.description
    ).forEach { (value, icon) ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "Action",
                tint = textColor,
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = value.ifEmpty { "..." },
                style = when (icon) {
                    R.drawable.hashtag -> Typography.titleMedium.copy(fontWeight = FontWeight.Black)
                    R.drawable.caption -> Typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    else -> Typography.bodyMedium
                },
                fontFamily = fontStyle,
                textAlign = textAlign,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = textColor,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (icon != R.drawable.description) {
            HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp), color = textColor)
        }
    }
}