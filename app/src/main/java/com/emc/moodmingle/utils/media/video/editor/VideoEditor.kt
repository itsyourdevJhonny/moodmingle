package com.emc.moodmingle.utils.media.video.editor

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.emc.moodmingle.utils.components.ScaffoldHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import androidx.core.graphics.scale
import androidx.media3.exoplayer.DefaultRenderersFactory
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodEntity

@OptIn(UnstableApi::class)
@Composable
fun VideoEditor(mood: DailyMoodEntity, videoUri: Uri, onDismiss: () -> Unit) {
    val context = LocalContext.current

    var selectedAction by remember { mutableStateOf("") }

    var state by remember(videoUri) { mutableStateOf(/*VideoEditorState()*/mood.media.video) }
    var videoFrames by remember { mutableStateOf<List<Bitmap>>(emptyList()) }

    val exoPlayer = remember(videoUri) {
        val renderersFactory = DefaultRenderersFactory(context).setEnableDecoderFallback(true)

        ExoPlayer.Builder(context)
            .setRenderersFactory(renderersFactory)
            .build().apply {
                setMediaItem(MediaItem.fromUri(videoUri))
                prepare()
            }
    }

    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
    DisposableEffect(exoPlayer) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> exoPlayer.play()
                else -> {}
            }
        }
        val lifecycle = lifecycleOwner.value.lifecycle
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }

    LaunchedEffect(exoPlayer) {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    val duration = exoPlayer.duration
                    if (duration > 0 && state.durationMs != duration) {
                        state = state.copy(durationMs = duration, endMs = duration)
                    }
                }
            }
        })
    }

    LaunchedEffect(state.startMs, state.endMs) {
        while (coroutineContext.isActive) {
            delay(100)
            if (exoPlayer.currentPosition >= state.endMs) {
                exoPlayer.seekTo(state.startMs)
            }
        }
    }

    LaunchedEffect(state.speed) {
        exoPlayer.playbackParameters = PlaybackParameters(state.speed)
    }

    LaunchedEffect(state.volume) {
        exoPlayer.volume = state.volume
    }

    // This effect will run once and extract the video frames as bitmaps
    LaunchedEffect(videoUri, state.durationMs) {
        // We only extract frames once the duration is known and we haven't done it yet
        if (state.durationMs > 0 && videoFrames.isEmpty()) {
            // Launch a coroutine on a background thread for this intensive work
            withContext(Dispatchers.IO) {
                val retriever = MediaMetadataRetriever()
                try {
                    retriever.setDataSource(context, videoUri)
                    val frames = mutableListOf<Bitmap>()
                    // Extract a frame every 2 seconds (2000 ms). Adjust as needed.
                    val intervalMs = 2000L
                    for (timeMs in 0L..state.durationMs step intervalMs) {
                        // Get a frame, scaled down for performance.
                        // The height (e.g., 50px) should match your TrimBar's height.
                        retriever.getFrameAtTime(
                            timeMs * 1000, // Time in microseconds
                            MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                        )?.let {
                            val scaledBitmap = it.scale(it.width / 4, 50, false)
                            frames.add(scaledBitmap)
                        }
                    }
                    videoFrames = frames
                } catch (e: Exception) {
                    // Handle exceptions, e.g., file not found
                    e.printStackTrace()
                } finally {
                    retriever.release()
                }
            }
        }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = { ScaffoldHeader(title = "Edit Video") { onDismiss() } },
        bottomBar = { VideoEditorFooter(selectedAction) { selectedAction = it } },
        floatingActionButton = {
            VideoEditorFloatingAction(
                selectedAction,
                state,
                exoPlayer,
                videoFrames,
                onStateChanged = { state = it }
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        VideoEditorContent(paddingValues, exoPlayer, state)
    }
}