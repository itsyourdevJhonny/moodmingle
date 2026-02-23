package com.emc.moodmingle.ui.post

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.create.util.formatDuration
import com.emc.moodmingle.ui.post.audio.AudioCache
import com.emc.moodmingle.ui.post.skeleton.PostSkeletonItem
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.local.PostViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(UnstableApi::class)
@Composable
fun PostVideo(videoUrl: String, viewModel: PostViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showFullVideo by remember { mutableStateOf(false) }
    val cachedThumbnail = viewModel.post.getCachedThumbnail(videoUrl)
    var thumbnail by remember(videoUrl) { mutableStateOf(cachedThumbnail) }
    var durationText by remember { mutableStateOf("") }
    val videoUri = videoUrl.toUri()

    var isPlaying by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }

    val videoPlayers = remember { mutableMapOf<String, ExoPlayer>() }
    var mainPlayerView: PlayerView? by remember { mutableStateOf(null) }

    var didDoubleTap by remember { mutableStateOf(false) }
    var isDoubleTappedFromLeft by remember { mutableStateOf(false) }
    val seekMs = 5000L // 10 seconds

    LaunchedEffect(videoUrl) {
        if (thumbnail == null) {
            val generated = getVideoThumbnail(videoUrl)
            if (generated != null) {
                viewModel.post.cacheThumbnail(videoUrl, generated)
                thumbnail = generated
            }
        }
    }

    LaunchedEffect(videoUrl) {
        val cacheDataSource = CacheDataSource.Factory()
            .setCache(AudioCache.get(context))
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        val exoPlayer = ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSource))
            .build()
            .apply {
                setMediaItem(MediaItem.fromUri(videoUri))
                prepare()
            }

        while (durationText.isEmpty()) {
            val durationMs = exoPlayer.duration
            if (durationMs > 0) {
                durationText = "%02d:%02d".format(durationMs / 1000 / 60, (durationMs / 1000) % 60)
            }
            delay(100)
        }
    }

    val cacheDataSource = CacheDataSource.Factory()
        .setCache(AudioCache.get(context))
        .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    val exoPlayer = videoPlayers[videoUrl] ?: ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSource))
        .build()
        .also { newPlayer ->
            val mediaItem = MediaItem.fromUri(videoUrl)
            newPlayer.setMediaItem(mediaItem)
            newPlayer.prepare()
            videoPlayers[videoUrl] = newPlayer
        }

    LaunchedEffect(exoPlayer) {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    isPlaying = false
                }
            }
        })
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
            videoPlayers.forEach { _, player ->
                player.release()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 360.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    mainPlayerView?.player = null
                    showFullVideo = true
                })
            }
    ) {
        if (!isPlaying) {
            if (thumbnail != null) {
                Image(
                    bitmap = thumbnail!!.asImageBitmap(),
                    contentDescription = "Video thumbnail",
                    modifier = Modifier
                        .fillMaxSize()
                        .heightIn(max = 360.dp),
                    contentScale = ContentScale.Crop
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.video),
                        contentDescription = "Image",
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer(alpha = 0.7f)
                            .drawGradient()
                    )

                    Box(
                        modifier = Modifier
                            .heightIn(min = 20.dp)
                            .widthIn(min = 36.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (durationText.isEmpty()) {
                            Box(
                                modifier = Modifier.height(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    strokeWidth = 1.dp,
                                    modifier = Modifier.size(12.dp),
                                    color = Color.White
                                )
                            }
                        } else {
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .align(Alignment.TopCenter),
                                text = durationText,
                                color = Color.White,
                                fontSize = 8.sp,
                            )
                        }
                    }
                }
            } else {
                PostSkeletonItem()
            }
        } else {
            if (exoPlayer.playbackState == Player.STATE_IDLE && exoPlayer.playbackState == Player.STATE_BUFFERING) {
                PostSkeletonItem()
            } else {
                AndroidView(
                    factory = {
                        PlayerView(it).apply {
                            player = exoPlayer
                            useController = false
                            mainPlayerView = this
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(120.dp)
                    .align(Alignment.CenterStart)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                didDoubleTap = true
                                isDoubleTappedFromLeft = true
                                val newPos =
                                    (exoPlayer.currentPosition - seekMs).coerceAtLeast(0L)
                                exoPlayer.seekTo(newPos)

                                scope.launch {
                                    delay(500)
                                    didDoubleTap = false
                                }
                            }
                        )
                    }
            )
            {
                if (didDoubleTap && isDoubleTappedFromLeft) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.backward_sound),
                            contentDescription = "Forward",
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = formatDuration(exoPlayer.currentPosition),
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.4f))
                        )
                    }
                }
            }

            //-----------------------------------------
            // RIGHT DOUBLE TAP AREA (SEEK FORWARD)
            //-----------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(120.dp)
                    .align(Alignment.CenterEnd)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                didDoubleTap = true
                                isDoubleTappedFromLeft = false
                                val newPos = exoPlayer.currentPosition + seekMs
                                exoPlayer.seekTo(newPos)

                                scope.launch {
                                    delay(500)
                                    didDoubleTap = false
                                }
                            }
                        )
                    }
            ) {
                if (didDoubleTap && !isDoubleTappedFromLeft) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.forward_sound),
                            contentDescription = "Forward",
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = formatDuration(exoPlayer.currentPosition),
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.4f))
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = isPlaying,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-8).dp, y = (-4).dp),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Icon(
                    painter = painterResource(if (isMuted) R.drawable.pause_sound else R.drawable.play_sound),
                    contentDescription = "Mute/Unmute",
                    tint = Color.White,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            isMuted = !isMuted
                            exoPlayer.volume = if (isMuted) 0f else 1f
                        }
                )
            }
        }

        if (thumbnail != null) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .padding(top = 4.dp, end = 8.dp)
                    .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
                    contentDescription = "Play/Pause",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            if (!isPlaying) {
                                exoPlayer.seekTo(0)
                                exoPlayer.play()
                            } else {
                                exoPlayer.pause()
                            }
                            isPlaying = !isPlaying
                        },
                    tint = Color.White
                )
            }
        }
    }

    if (showFullVideo) {
        Dialog(onDismissRequest = { showFullVideo = false }) {
            FullVideoPlayer(
                exoPlayer = exoPlayer,
                onClose = {
                    showFullVideo = false
                    if (exoPlayer.isPlaying) {
                        mainPlayerView?.player = exoPlayer
                        isPlaying = true
                    }
                }
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun FullVideoPlayer(onClose: () -> Unit, exoPlayer: ExoPlayer) {
    if (!exoPlayer.isPlaying) {
        exoPlayer.play()
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
}

suspend fun getVideoThumbnail(videoUrl: String): Bitmap? {
    return withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(videoUrl, HashMap())
            retriever.getFrameAtTime(1_000_000)
        } catch (_: Exception) {
            null
        } finally {
            retriever.release()
        }
    }
}