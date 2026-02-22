package com.emc.moodmingle.utils.media

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.create.util.getMimeType
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.exoplayer.ExoPlayerUtils
import com.emc.moodmingle.utils.media.image.ImageUtils
import com.emc.moodmingle.utils.modifier.drawGradient
import kotlin.collections.forEach

class MediaUtils {

    @Composable
    fun SingleMedia(uri: Uri, maxHeight: Dp, rounded: Boolean = true) {
        Box(
            modifier = Modifier
                .heightIn(max = maxHeight)
                .clip(if (rounded) RoundedCornerShape(8.dp) else RectangleShape)
        ) {
            DisplayMedia(uri)
        }
    }

    @Composable
    fun DoubleMedia(uris: List<Uri>, height: Dp, width: Dp, rounded: Boolean = true) {
        Row(modifier = Modifier.clip(if (rounded) RoundedCornerShape(8.dp) else RectangleShape)) {
            uris.forEach { uri ->
                Box(modifier = Modifier.size(height = height, width = width)) {
                    DisplayMedia(uri)
                }
            }
        }
    }

    @Composable
    fun TripleMedia(uris: List<Uri>, height: Dp, width: Dp, size: Dp, rounded: Boolean = true) {
        Row(modifier = Modifier.clip(if (rounded) RoundedCornerShape(8.dp) else RectangleShape)) {
            Box(modifier = Modifier.size(height = height, width = width)) {
                DisplayMedia(uris[0])
            }

            Column {
                Box(modifier = Modifier.size(size)) {
                    DisplayMedia(uris[1])
                }
                Box(modifier = Modifier.size(size)) {
                    DisplayMedia(uris[2])
                }
            }
        }
    }

    @Composable
    fun MultipleMedia(uris: List<Uri>, size: Dp, rounded: Boolean = true) {
        Row(modifier = Modifier.clip(if (rounded) RoundedCornerShape(8.dp) else RectangleShape)) {
            Column {
                Box(modifier = Modifier.size(size)) {
                    DisplayMedia(uris[0])
                }
                Box(modifier = Modifier.size(size)) {
                    DisplayMedia(uris[1])
                }
            }
            Column {
                Box(modifier = Modifier.size(size)) {
                    DisplayMedia(uris[2])
                }
                Box(
                    modifier = Modifier.size(size),
                    contentAlignment = Alignment.Center
                ) {
                    DisplayMedia(uris[3])
                    if (uris.size > 4) {
                        MoreMediaCount(count = uris.size - 4)
                    }
                }
            }
        }
    }

    @Composable
    fun DisplayMedia(uri: Uri) {
        val context = LocalContext.current
        val mimeType = getMimeType(context, uri) ?: ""

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                mimeType.startsWith("image") -> ImageMedia(uri)
                mimeType.startsWith("video") -> VideoMedia(uri)
                mimeType.startsWith("audio") -> AudioMedia(uri)
            }

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .offset(x = 4.dp, y = 4.dp)
                    .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        when {
                            mimeType.startsWith("image") -> R.drawable.image
                            mimeType.startsWith("video") -> R.drawable.video
                            else -> R.drawable.audio
                        }
                    ),
                    contentDescription = "Image",
                    modifier = Modifier
                        .size(20.dp)
                        .drawGradient()
                )
            }
        }
    }

    @Composable
    fun MoreMediaCount(count: Int) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "+$count", style = Typography.bodyLarge.copy(color = Color.White))
        }
    }

    @Composable
    fun ImageMedia(uri: Uri) {
        AsyncImage(
            model = uri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }

    @Composable
    fun VideoMedia(uri: Uri, frameMillis: Long = 1_000L) {
        val context = LocalContext.current
        val imageLoader = remember { ImageUtils.provideVideoImageLoader(context) }
        var durationText by remember { mutableStateOf("") }

        LaunchedEffect(uri) {
            ExoPlayerUtils.getDurationTextFrom(
                uri as Any,
                context,
                durationText,
                onDuration = { durationText = it }
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(uri)
                    .videoFrameMillis(frameMillis)
                    .crossfade(true)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            DisplayDuration(durationText, Modifier.align(Alignment.BottomEnd))
        }
    }

    @Composable
    fun AudioMedia(uri: Uri) {
        val context = LocalContext.current
        var durationText by remember { mutableStateOf("") }

        LaunchedEffect(uri) {
            ExoPlayerUtils.getDurationTextFrom(
                uri as Any,
                context,
                durationText,
                onDuration = { durationText = it }
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(R.drawable.music_disk),
                contentDescription = null,
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )

            DisplayDuration(durationText, Modifier.align(Alignment.Center))
        }
    }

    @Composable
    fun DisplayDuration(durationText: String, modifier: Modifier = Modifier) {
        if (durationText.isEmpty()) {
            Box(contentAlignment = Alignment.Center, modifier = modifier) {
                CircularProgressIndicator(
                    strokeWidth = 1.dp,
                    modifier = Modifier.size(12.dp),
                    color = Color.White
                )
            }
        } else {
            Text(
                text = durationText,
                style = Typography.bodySmall.copy(color = Color.White),
                modifier = modifier
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(vertical = 4.dp, horizontal = 6.dp)
            )
        }
    }
}