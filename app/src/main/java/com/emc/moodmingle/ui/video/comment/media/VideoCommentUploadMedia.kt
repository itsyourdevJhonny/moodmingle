package com.emc.moodmingle.ui.video.comment.media

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.modifier.roundedGrayBorder

@Composable
fun VideoCommentUploadMedia(
    mediaUris: List<Uri>,
    isSelected: Boolean,
    onSelectedUris: (List<Uri>) -> Unit,
    onSelected: (Boolean) -> Unit
) {
    val media = listOf(
        R.drawable.image to "Image",
        R.drawable.video to "Video",
        R.drawable.audio to "Audio"
    )

    var selectedMediaType by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.padding(top = 8.dp, start = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        media.forEach { (iconRes, text) ->
            Box(
                modifier = Modifier
                    .background(SecondaryDark, RoundedCornerShape(8.dp))
                    .roundedGrayBorder(8.dp)
                    .clickable { selectedMediaType = text }
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = text,
                        modifier = Modifier
                            .size(20.dp)
                            .drawGradient()
                    )

                    Text(text = text, style = Typography.bodySmall.copy(color = Color.White))
                }
            }
        }
    }

    if (selectedMediaType.isNotBlank()) {
        VideoCommentSelectMedia(
            mediaUris,
            selectedMediaType,
            isSelected,
            onSelectedUris,
            onSelectedMediaType = { selectedMediaType = it },
            onSelected
        )
    }
}