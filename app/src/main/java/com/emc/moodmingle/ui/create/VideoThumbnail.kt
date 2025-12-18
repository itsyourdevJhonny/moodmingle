package com.emc.moodmingle.ui.create

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cloudinary.android.uploadwidget.utils.UriUtils.getVideoThumbnail
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.graphics.scale

@Composable
fun VideoThumbnail(videoUri: Uri?) {
    val context = LocalContext.current
    // cache thumbnails globally in memory to avoid recomputation
    val thumbnailCache = remember { mutableStateMapOf<Uri, Bitmap?>() }
    var thumbnail by remember(videoUri) { mutableStateOf<Bitmap?>(null) }

    // async loading of thumbnail
    LaunchedEffect(videoUri) {
        if (videoUri != null && !thumbnailCache.containsKey(videoUri)) {
            val bmp = withContext(Dispatchers.IO) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        context.contentResolver.loadThumbnail(videoUri, Size(320, 180), null)
                    } else {
                        retrieveVideoFrame(context, videoUri)?.scale(320, 180)
                    }
                } catch (e: Exception) { null }
            }
            thumbnailCache[videoUri] = bmp
        }
        thumbnail = thumbnailCache[videoUri]
    }

    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                brush = BrushPrimaryGradient,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (thumbnail != null) {
            AsyncImage(
                model = thumbnail,
                contentDescription = "Video Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        }
    }
}

/*@Composable
fun VideoThumbnail(videoUri: Uri?) {
    val context = LocalContext.current
    val thumbnail by remember(videoUri) {
        mutableStateOf(getVideoThumbnail(context, videoUri))
    }

    thumbnail?.let {
        AsyncImage(
            model = it,
            contentDescription = "Video Thumbnail",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    brush = BrushPrimaryGradient,
                    shape = RoundedCornerShape(8.dp)
                )
        )
    }
}*/

/*
private fun getVideoThumbnail(context: Context, videoUri: Uri): Bitmap? {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(context, videoUri)
        // get frame at 1st second (1_000_000 microseconds)
        retriever.getFrameAtTime(1_000_000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        retriever.release()
    }
}*/
