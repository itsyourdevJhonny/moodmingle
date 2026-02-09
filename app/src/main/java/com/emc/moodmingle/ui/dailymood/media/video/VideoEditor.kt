package com.emc.moodmingle.ui.dailymood.media.video

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Effect
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.ScaleAndRotateTransformation
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.transformer.Transformer
import androidx.media3.ui.PlayerView
import com.emc.moodmingle.viewmodel.ui.VideoEditorViewModel
import java.io.File

@OptIn(UnstableApi::class)
@Composable
fun VideoEditor(videoUri: Uri) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<VideoEditorViewModel>()

    val state by viewModel.state.collectAsStateWithLifecycle()

    val player = rememberVideoPlayer(context, videoUri)

    // apply effects reactively (NO recreation)
    LaunchedEffect(state) {
        player.setPlaybackSpeed(state.speed)
        player.setVideoEffects(videoEffects(state))
        player.volume = if (state.mute) 0f else 1f
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    Column(Modifier.fillMaxSize()) {

        // yes, AndroidView is STILL REQUIRED
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            factory = {
                PlayerView(it).apply {
                    this.player = player
                    useController = true
                }
            }
        )

        EditorControls(state, viewModel::update)
    }
}

@Composable
fun EditorControls(
    state: VideoEditorState,
    onUpdate: ((VideoEditorState) -> VideoEditorState) -> Unit,
) {
    Column(Modifier.padding(16.dp)) {

        Text("Speed ${state.speed}x")
        Slider(
            value = state.speed,
            valueRange = 0.25f..2f,
            onValueChange = {
                onUpdate { it.copy(speed = it.speed) }
            }
        )

        Text("Brightness")
        Slider(
            value = state.brightness,
            valueRange = -1f..1f,
            onValueChange = {
                onUpdate { it.copy(brightness = it.brightness) }
            }
        )

        Text("Saturation")
        Slider(
            value = state.saturation,
            valueRange = 0f..2f,
            onValueChange = {
                onUpdate { it.copy(saturation = it.saturation) }
            }
        )

        Row {
            Button(onClick = {
                onUpdate { it.copy(rotation = it.rotation + 90f) }
            }) { Text("Rotate") }

            Spacer(Modifier.width(8.dp))

            Button(onClick = {
                onUpdate { it.copy(flipX = !it.flipX) }
            }) { Text("Flip") }
        }
    }
}

@OptIn(UnstableApi::class)
fun exportVideo(
    context: Context,
    uri: Uri,
    state: VideoEditorState,
    onDone: (Uri) -> Unit,
) {
    val output = File(context.cacheDir, "export_${System.currentTimeMillis()}.mp4")

    val item = MediaItem.Builder()
        .setUri(uri)
        .setClippingConfiguration(
            MediaItem.ClippingConfiguration.Builder()
                .setStartPositionMs(state.trimStartMs)
                .setEndPositionMs(state.trimEndMs)
                .build()
        )
        .build()

    Transformer.Builder(context).build()
        .start(item, output.absolutePath)

    onDone(output.toUri())
}

@Composable
fun rememberVideoPlayer(
    context: Context,
    uri: Uri,
): ExoPlayer {
    return remember(uri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = true
        }
    }
}

@OptIn(UnstableApi::class)
fun videoEffects(state: VideoEditorState): List<Effect> {
    val effects = mutableListOf<Effect>()

    if (state.rotation != 0f || state.flipX) {
        effects += ScaleAndRotateTransformation.Builder()
            .setRotationDegrees(state.rotation)
            .setScale(if (state.flipX) -1f else 1f, 1f)
            .build()
    }

    return effects
}

@Stable
data class VideoEditorState(
    val speed: Float = 1f,
    val brightness: Float = 0f,
    val saturation: Float = 1f,
    val rotation: Float = 0f,
    val flipX: Boolean = false,
    val mute: Boolean = false,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = Long.MAX_VALUE,
)