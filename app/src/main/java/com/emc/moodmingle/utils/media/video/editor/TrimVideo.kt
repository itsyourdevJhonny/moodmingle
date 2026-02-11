package com.emc.moodmingle.utils.media.video.editor

import android.graphics.Bitmap
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import kotlin.collections.forEach
import kotlin.math.roundToLong

@Composable
fun TrimVideo(
    state: VideoEditorState,
    exoPlayer: ExoPlayer,
    videoFrames: List<Bitmap>,
    onStateChanged: (VideoEditorState) -> Unit,
) {
    if (state.durationMs > 0) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TrimRange(state)

            TrimBar(
                durationMs = state.durationMs,
                startMs = state.startMs,
                endMs = state.endMs,
                videoFrames = videoFrames,
                onTrimChanged = { newStart, newEnd ->
                    onStateChanged(state.copy(startMs = newStart, endMs = newEnd))
                    if (exoPlayer.currentPosition < newStart || exoPlayer.currentPosition > newEnd) {
                        exoPlayer.seekTo(newStart)
                    }
                }
            )
        }
    }
}

@Composable
private fun TrimRange(state: VideoEditorState) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            .padding(vertical = 6.dp, horizontal = 12.dp)
            .animateContentSize()
    ) {
        Text(text = "Trim Range:", color = Color.White)

        Text(
            text = "%.1fs - %.1fs".format(state.startMs / 1000f, state.endMs / 1000f),
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TrimBar(
    durationMs: Long,
    startMs: Long,
    endMs: Long,
    videoFrames: List<Bitmap>,
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
            .height(46.dp)
            .onSizeChanged { barWidthPx = it.width.toFloat() }
            .background(Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
    ) {
        VideoFrames(videoFrames)

        if (barWidthPx > 0) {
            // Highlighted selected range
            SelectedRange(startOffset, endOffset, handleWidthPx)

            // Start Handle
            TrimHandle(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset { IntOffset(startOffset.toInt(), 0) }
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { _, dragAmount ->
                            val newStartOffset = (startOffset + dragAmount).coerceIn(
                                0f, endOffset - (msToPx(minTimeRangeMs))
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
private fun BoxScope.SelectedRange(startOffset: Float, endOffset: Float, handleWidthPx: Float) {
    Box(
        modifier = Modifier
            .align(Alignment.CenterStart)
            .offset { IntOffset(x = startOffset.toInt() + (handleWidthPx / 2).toInt(), y = 0) }
            .width(with(LocalDensity.current) { (endOffset - startOffset).toDp() })
            .fillMaxHeight()
            .border(2.dp, Color.White, RoundedCornerShape(4.dp))
            .background(Color.White.copy(alpha = 0.3f), shape = RoundedCornerShape(4.dp))
    )
}

@Composable
private fun VideoFrames(videoFrames: List<Bitmap>) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
    ) {
        videoFrames.forEach { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Video Frame",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
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