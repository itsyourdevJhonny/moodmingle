package com.emc.moodmingle.ui.dailymood.media.video

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun VideoEditor(
    videoUri: Uri
) {
    val context = LocalContext.current
    var state by remember { mutableStateOf(EditorState()) }

    val player = remember(videoUri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
            playWhenReady = true
        }
    }

    // listen for duration
    LaunchedEffect(Unit) {
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    val d = player.duration
                    state = state.copy(
                        durationMs = d,
                        startMs = 0L,
                        endMs = d
                    )
                }
            }
        })
    }

    // enforce trim bounds + speed
    LaunchedEffect(state.startMs, state.endMs, state.speed) {
        player.setPlaybackSpeed(state.speed)

        if (player.currentPosition < state.startMs) {
            player.seekTo(state.startMs)
        }

        player.addListener(object : Player.Listener {
            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                if (newPosition.positionMs > state.endMs) {
                    player.seekTo(state.startMs)
                }
            }
        })
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    Column(Modifier.fillMaxSize()) {

        // VIDEO PREVIEW
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            factory = {
                PlayerView(it).apply {
                    this.player = player
                    useController = true
                }
            }
        )

        Spacer(Modifier.height(12.dp))

        // SPEED SLIDER (REAL-TIME)
        Text("Speed: ${"%.2f".format(state.speed)}x")
        Slider(
            value = state.speed,
            valueRange = 0.25f..2f,
            onValueChange = {
                state = state.copy(speed = it)
            }
        )

        Spacer(Modifier.height(12.dp))

        // FACEBOOK-STYLE TRIM
        if (state.durationMs > 0) {
            FacebookTrimBar(
                durationMs = state.durationMs,
                startMs = state.startMs,
                endMs = state.endMs,
                onTrimChanged = { start, end ->
                    state = state.copy(startMs = start, endMs = end)
                    player.seekTo(start)
                }
            )
        }

        Text(
            "Start: ${state.startMs / 1000}s  |  End: ${state.endMs / 1000}s",
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun FacebookTrimBar(
    durationMs: Long,
    startMs: Long,
    endMs: Long,
    onTrimChanged: (Long, Long) -> Unit
) {
    var widthPx by remember { mutableFloatStateOf(1f) }

    val startFraction = startMs / durationMs.toFloat()
    val endFraction = endMs / durationMs.toFloat()
    val minGap = 0.05f // 5%

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .onSizeChanged { widthPx = it.width.toFloat() }
            .background(Color.DarkGray, RoundedCornerShape(8.dp))
    ) {

        // SELECTED RANGE
        Box(
            modifier = Modifier
                .offset { IntOffset((startFraction * widthPx).toInt(), 0) }
                .width(((endFraction - startFraction) * widthPx).dp)
                .fillMaxHeight()
                .background(Color.White.copy(alpha = 0.3f))
        )

        // START HANDLE
        TrimHandle(
            xPx = startFraction * widthPx,
            onDrag = { dx ->
                val newStart =
                    ((startFraction + dx / widthPx)
                        .coerceIn(0f, endFraction - minGap)
                            * durationMs).toLong()

                onTrimChanged(newStart, endMs)
            }
        )

        // END HANDLE
        TrimHandle(
            xPx = endFraction * widthPx,
            onDrag = { dx ->
                val newEnd =
                    ((endFraction + dx / widthPx)
                        .coerceIn(startFraction + minGap, 1f)
                            * durationMs).toLong()

                onTrimChanged(startMs, newEnd)
            }
        )
    }
}

@Composable
fun TrimHandle(
    xPx: Float,
    onDrag: (dxPx: Float) -> Unit
) {
    Box(
        modifier = Modifier
            .offset { IntOffset(xPx.toInt() - 16, 0) }
            .width(32.dp)
            .fillMaxHeight()
            .background(Color.White, RoundedCornerShape(6.dp))
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    onDrag(dragAmount)
                }
            }
    )
}

data class TrimState(
    val startMs: Long,
    val endMs: Long,
    val durationMs: Long
)

data class EditorState(
    val startMs: Long = 0L,
    val endMs: Long = 0L,
    val durationMs: Long = 0L,
    val speed: Float = 1f
)

data class SimpleVideoEditState(
    val speed: Float = 1f,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = C.TIME_END_OF_SOURCE,
)