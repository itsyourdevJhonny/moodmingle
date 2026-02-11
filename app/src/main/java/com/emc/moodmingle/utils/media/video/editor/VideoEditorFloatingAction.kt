package com.emc.moodmingle.utils.media.video.editor

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.media3.exoplayer.ExoPlayer

@Composable
fun VideoEditorFloatingAction(
    selectedAction: String,
    state: VideoEditorState,
    exoPlayer: ExoPlayer,
    videoFrames: List<Bitmap>,
    onStateChanged: (VideoEditorState) -> Unit,
) {
    Box {
        when (selectedAction) {
            "Speed" -> EditVideoSpeed(state, onStateChanged)
            "Trim" -> TrimVideo(state, exoPlayer, videoFrames, onStateChanged)
            "Volume" -> EditVideoVolume(state, onStateChanged)
        }
    }
}