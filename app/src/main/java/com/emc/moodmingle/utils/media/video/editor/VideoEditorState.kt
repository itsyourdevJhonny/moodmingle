package com.emc.moodmingle.utils.media.video.editor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoEditorState(
    val startMs: Long = 0L,
    val endMs: Long = 0L,
    val durationMs: Long = 0L,
    val speed: Float = 1f,
    val volume: Float = 1f,
    val rotation: Float = 360f
) : Parcelable
