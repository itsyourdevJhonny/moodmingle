package com.emc.moodmingle.ui.post.audio

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.create.AllMediaGallery
import com.emc.moodmingle.ui.create.formatDuration
import com.emc.moodmingle.ui.create.getFileName
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.Typography

@Composable
fun FilePicker(
    mediaUris: List<Uri>,
    onSelectedType: (String) -> Unit,
    onUploadedUri: (List<Uri>) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(top = 23.dp)
            .background(BrushPrimaryGradient, RoundedCornerShape(8.dp))
            .clickable { showDialog = true },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val uploadMedia = listOf(
            R.drawable.image to "Image",
            R.drawable.video to "Video",
            R.drawable.audio to "Audio"
        )

        uploadMedia.forEach { upload ->
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(upload.first),
                    contentDescription = upload.second,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            if (upload.second != "Audio") {
                Text(text = "/")
            }
        }
    }

    if (showDialog) {
        AllMediaGallery(
            mediaUris,
            onSelectedType = onSelectedType,
            onDismiss = { showDialog = it },
            onUploadedUri = onUploadedUri
        )
    }
}

@Composable
fun AudioItemMini(
    uri: Uri,
    isPlaying: Boolean,
    onClickPlay: () -> Unit,
    onClickPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .background(BrushPrimaryGradient, RoundedCornerShape(8.dp))
            .padding(horizontal = 4.dp)
            .size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val iconRes = if (isPlaying) R.drawable.pause else R.drawable.play

            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { if (isPlaying) onClickPause() else onClickPlay() }
            )

            Text(
                text = getFileName(context, uri),
                style = Typography.bodySmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }

        var duration by remember { mutableLongStateOf(0L) }

        LaunchedEffect(Unit) {
            val retriever = android.media.MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, uri)
                duration =
                    retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLong() ?: 0L
            } catch (_: Exception) {
                duration = 0L
            }
            retriever.release()
        }

        Text(
            text = formatDuration(duration),
            style = Typography.bodySmall.copy(color = Color.White),
            modifier = Modifier
                .padding(vertical = 4.dp)
                .align(Alignment.BottomEnd)
        )
    }
}
