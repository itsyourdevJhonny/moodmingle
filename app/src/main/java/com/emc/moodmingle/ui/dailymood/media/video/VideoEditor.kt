package com.emc.moodmingle.ui.dailymood.media.video

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.emc.moodmingle.utils.components.ScaffoldHeader
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.roundToLong

@OptIn(UnstableApi::class)
@Composable
fun VideoEditor(videoUri: Uri, onDismiss: () -> Unit) {
    val context = LocalContext.current

    var state by remember(videoUri) { mutableStateOf(EditorState()) }

    val exoPlayer = remember(videoUri) {
        ExoPlayer.Builder(context).build().apply {
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
                        state = state.copy(
                            durationMs = duration,
                            endMs = duration
                        )
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

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            ScaffoldHeader(title = "Edit Video") { onDismiss() }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(Color.Black, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    factory = {
                        PlayerView(it).apply {
                            player = exoPlayer
                            useController = true
                            controllerShowTimeoutMs = 1500
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(24.dp))

            Text("Speed: ${"%.2f".format(state.speed)}x")

            Slider(
                value = state.speed,
                valueRange = 0.5f..10.0f,
                onValueChange = { newSpeed ->
                    state = state.copy(speed = newSpeed)
                }
            )

            Spacer(Modifier.height(24.dp))

            if (state.durationMs > 0) {
                FacebookTrimBar(
                    durationMs = state.durationMs,
                    startMs = state.startMs,
                    endMs = state.endMs,
                    onTrimChanged = { newStart, newEnd ->
                        state = state.copy(startMs = newStart, endMs = newEnd)
                        if (exoPlayer.currentPosition < newStart || exoPlayer.currentPosition > newEnd) {
                            exoPlayer.seekTo(newStart)
                        }
                    }
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Trim Range: %.1fs - %.1fs".format(state.startMs / 1000f, state.endMs / 1000f)
                )
            }
        }
    }
}

@Composable
private fun FacebookTrimBar(
    durationMs: Long,
    startMs: Long,
    endMs: Long,
    onTrimChanged: (Long, Long) -> Unit,
) {
    var barWidthPx by remember { mutableFloatStateOf(0f) }
    val handleWidthPx = with(LocalDensity.current) { 32.dp.toPx() }
    val minTimeRangeMs = (durationMs * 0.05f).roundToLong()

    val draggableWidth = remember(barWidthPx) { barWidthPx - handleWidthPx }

    fun msToPx(ms: Long): Float {
        if (durationMs == 0L || draggableWidth <= 0) return 0f
        return (ms.toFloat() / durationMs) * draggableWidth
    }

    fun pxToMs(px: Float): Long {
        if (draggableWidth <= 0) return 0L
        return (px / draggableWidth * durationMs).toLong()
    }

    var startOffset by remember { mutableFloatStateOf(msToPx(startMs)) }
    var endOffset by remember { mutableFloatStateOf(msToPx(endMs)) }

    // Update offsets if the external state changes
    LaunchedEffect(startMs, endMs, barWidthPx) {
        startOffset = msToPx(startMs)
        endOffset = msToPx(endMs)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .onSizeChanged { barWidthPx = it.width.toFloat() }
            .background(Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
    ) {
        if (barWidthPx > 0) {
            // Highlighted selected range
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset {
                        IntOffset(
                            x = startOffset.toInt() + (handleWidthPx / 2).toInt(),
                            y = 0
                        )
                    }
                    .width(with(LocalDensity.current) { (endOffset - startOffset).toDp() })
                    .fillMaxHeight()
                    .background(Color.White.copy(alpha = 0.3f), shape = RoundedCornerShape(4.dp))
            )

            // Start Handle
            TrimHandle(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset { IntOffset(startOffset.toInt(), 0) }
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { _, dragAmount ->
                            val newStartOffset = (startOffset + dragAmount).coerceIn(
                                0f,
                                endOffset - (msToPx(minTimeRangeMs))
                            )
                            startOffset = newStartOffset
                            onTrimChanged(pxToMs(newStartOffset), pxToMs(endOffset))
                        }
                    }
            )

            // End Handle
            TrimHandle(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset { IntOffset(endOffset.toInt(), 0) }
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { _, dragAmount ->
                            val newEndOffset = (endOffset + dragAmount).coerceIn(
                                startOffset + (msToPx(minTimeRangeMs)), draggableWidth
                            )
                            endOffset = newEndOffset
                            onTrimChanged(pxToMs(startOffset), pxToMs(newEndOffset))
                        }
                    }
            )
        }
    }
}

@Composable
private fun TrimHandle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(32.dp)
            .fillMaxHeight()
            .background(Color.White, RoundedCornerShape(6.dp))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(2.dp)
                .height(20.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(1.dp))
        )
    }
}


private data class EditorState(
    val startMs: Long = 0L,
    val endMs: Long = 0L,
    val durationMs: Long = 0L,
    val speed: Float = 1f,
)
