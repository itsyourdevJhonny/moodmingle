package com.emc.moodmingle.ui.post

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.widget.Toast
import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.scale
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import java.io.ByteArrayOutputStream

@Composable
fun PostVideo(@RawRes videoRes: Int) {
    var showFullVideo by remember { mutableStateOf(false) }

    val thumbnail = videoThumbnailFromRaw(videoRes)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .pointerInput(Unit) {
                detectTapGestures(onTap = { showFullVideo = true })
            }
    ) {
        VideoThumbnailAsyncImage(thumbnail)

        Icon(
            painter = painterResource(R.drawable.video),
            modifier = Modifier
                .align(Alignment.Center)
                .size(60.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush = BrushPrimaryGradient,
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                },
            contentDescription = "Video"
        )
    }

    if (showFullVideo) {
        Dialog(
            onDismissRequest = { showFullVideo = false }
        ) {
            FullVideoPlayer(videoRes = videoRes, onClose = { showFullVideo = false })
        }
    }
}

@Composable
fun VideoThumbnailAsyncImage(thumbnail: Bitmap?) {
    val resizedBitmap = thumbnail?.scale(200, 200)

    val stream = ByteArrayOutputStream()
    resizedBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    val byteArray = stream.toByteArray()

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(byteArray).build(),
        contentDescription = "Video thumbnail",
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop
    )
}

@SuppressLint("LocalContextResourcesRead")
@Composable
fun videoThumbnailFromRaw(@RawRes videoRes: Int): Bitmap? {
    val context = LocalContext.current
    return remember(videoRes) {
        try {
            val retriever = MediaMetadataRetriever()
            val fd = context.resources.openRawResourceFd(videoRes)
            retriever.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
            val bitmap = retriever.getFrameAtTime(0)
            retriever.release()
            fd.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun FullVideoPlayer(
    @RawRes videoRes: Int,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri("android.resource://${context.packageName}/$videoRes")
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = {
                    PlayerView(it).apply {
                        player = exoPlayer
                        useController = true
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(30.dp)
            ) {
                Icon(
                    modifier = Modifier.size(35.dp),
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    exoPlayer.addListener(object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            Log.e("VideoPlayer", "Playback error: ${error.message}")
            Toast.makeText(context, "Video format not supported", Toast.LENGTH_SHORT).show()
        }
    })
}