package com.emc.moodmingle.ui.dailymood.media.video

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.SpeedChangeEffect
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import androidx.media3.ui.PlayerView
import java.io.File

@Composable
fun VideoEditor(
    videoUri: Uri,
    modifier: Modifier = Modifier,
    onExported: (Uri) -> Unit = {},
) {
    val context = LocalContext.current

    var state by remember { mutableStateOf(SimpleVideoEditState()) }
    var durationMs by remember { mutableLongStateOf(0L) }

    val player = remember(videoUri) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUri)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    // apply speed in real time
    LaunchedEffect(state.speed) {
        player.setPlaybackSpeed(state.speed)
    }

    // get duration once
    LaunchedEffect(Unit) {
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    durationMs = player.duration
                    state = state.copy(trimEndMs = durationMs)
                }
            }
        })
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    Column(modifier.fillMaxSize()) {

        // VIDEO PREVIEW (YES ANDROIDVIEW IS REQUIRED)
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            factory = {
                PlayerView(it).apply {
                    this.player = player
                    useController = true
                }
            }
        )

        Spacer(Modifier.height(16.dp))

        // SPEED CONTROL
        Text("Playback Speed: ${"%.2f".format(state.speed)}x")
        Slider(
            value = state.speed,
            valueRange = 0.25f..2f,
            onValueChange = {
                state = state.copy(speed = it)
            }
        )

        Spacer(Modifier.height(16.dp))

        // TRIM START
        Text("Trim Start: ${state.trimStartMs / 1000}s")
        Slider(
            value = state.trimStartMs.toFloat(),
            valueRange = 0f..state.trimEndMs.toFloat(),
            onValueChange = {
                state = state.copy(trimStartMs = it.toLong())
                player.seekTo(it.toLong())
            }
        )

        // TRIM END
        Text("Trim End: ${state.trimEndMs / 1000}s")
        Slider(
            value = state.trimEndMs.toFloat(),
            valueRange = state.trimStartMs.toFloat()..durationMs.toFloat(),
            onValueChange = {
                state = state.copy(trimEndMs = it.toLong())
            }
        )

        Spacer(Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                exportTrimmedVideo(
                    context = context,
                    videoUri = videoUri,
                    state = state,
                    onExported = onExported
                )
            }
        ) {
            Text("Export Video")
        }
    }
}

@OptIn(UnstableApi::class)
fun exportTrimmedVideo(
    context: Context,
    videoUri: Uri,
    state: SimpleVideoEditState,
    onExported: (Uri) -> Unit,
) {
    val outputFile = File(
        context.cacheDir,
        "edited_${System.currentTimeMillis()}.mp4"
    )

    val mediaItem = MediaItem.Builder()
        .setUri(videoUri)
        .setClippingConfiguration(
            MediaItem.ClippingConfiguration.Builder()
                .setStartPositionMs(state.trimStartMs)
                .setEndPositionMs(state.trimEndMs)
                .build()
        )
        .build()

    val editedItem = EditedMediaItem.Builder(mediaItem)
        .setEffects(
            Effects(
                emptyList(),
                listOf(SpeedChangeEffect(state.speed))
            )
        )
        .build()

    val transformer = Transformer.Builder(context).build()

    transformer.start(editedItem, outputFile.absolutePath)

    transformer.addListener(object : Transformer.Listener {
        override fun onCompleted(
            composition: Composition,
            exportResult: ExportResult,
        ) {
            onExported(outputFile.toUri())
        }

        override fun onError(
            composition: Composition,
            exportResult: ExportResult,
            exception: ExportException,
        ) {
            exception.printStackTrace()
        }
    })
}

data class SimpleVideoEditState(
    val speed: Float = 1f,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = C.TIME_END_OF_SOURCE,
)