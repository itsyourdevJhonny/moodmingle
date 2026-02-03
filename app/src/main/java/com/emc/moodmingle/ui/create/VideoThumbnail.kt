package com.emc.moodmingle.ui.create

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.graphics.scale
import com.emc.moodmingle.ui.theme.Typography

@Composable
fun VideoThumbnail(videoUri: Uri?) {
    val context = LocalContext.current
    val thumbnailCache = remember { mutableStateMapOf<Uri, Bitmap?>() }
    var thumbnail by remember(videoUri) { mutableStateOf<Bitmap?>(null) }
    var duration by remember { mutableLongStateOf(0L) }

    LaunchedEffect(videoUri) {
        if (videoUri != null && !thumbnailCache.containsKey(videoUri)) {
            val bmp = withContext(Dispatchers.IO) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        context.contentResolver.loadThumbnail(videoUri, Size(320, 180), null)
                    } else {
                        retrieveVideoFrame(context, videoUri)?.scale(320, 180)
                    }
                } catch (_: Exception) {
                    null
                }
            }
            thumbnailCache[videoUri] = bmp
        }
        thumbnail = thumbnailCache[videoUri]
    }

    LaunchedEffect(Unit) {
        val retriever = android.media.MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, videoUri)
            duration =
                retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLong() ?: 0L
        } catch (_: Exception) {
            duration = 0L
        }
        retriever.release()
    }

    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(width = 0.5.dp, brush = BrushPrimaryGradient, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (thumbnail != null) {
            AsyncImage(
                model = thumbnail,
                contentDescription = "Video Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Text(
                text = formatDuration(duration),
                style = Typography.bodySmall.copy(color = Color.White, fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 8.dp)
                    .align(Alignment.BottomEnd)
            )
        } else {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        }
    }
}