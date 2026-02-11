package com.emc.moodmingle.utils.media.video.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.session.R
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVideoSpeed(state: VideoEditorState, onStateChanged: (VideoEditorState) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                .padding(vertical = 6.dp, horizontal = 12.dp)
                .animateContentSize()
        ) {
            Text(text = "Speed:", color = Color.White)

            Text(
                text = "${"%.2f".format(state.speed)}x",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        ResetButton(state, onStateChanged)

        SpeedSlider(state, onStateChanged)
    }
}

@Composable
private fun ColumnScope.ResetButton(
    state: VideoEditorState,
    onStateChanged: (VideoEditorState) -> Unit,
) {
    AnimatedVisibility(visible = state.speed != 1f) {
        TextButton(
            onClick = { onStateChanged(VideoEditorState(speed = 1f)) },
            colors = ButtonDefaults.textButtonColors(
                containerColor = Color.Black.copy(alpha = 0.3f),
                contentColor = Color.White
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.media3_icon_rewind),
                contentDescription = null
            )
            Text(text = " Reset")
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SpeedSlider(state: VideoEditorState, onStateChanged: (VideoEditorState) -> Unit) {
    Slider(
        value = state.speed,
        valueRange = 0.5f..10.0f,
        onValueChange = { onStateChanged(VideoEditorState(speed = it)) },
        colors = SliderDefaults.colors(
            activeTrackColor = SecondaryDark,
            inactiveTrackColor = Color.Gray.copy(alpha = 0.8f),
            thumbColor = Color.White
        ),
        track = { sliderState -> SliderTrack(sliderState) },
        thumb = { SliderThumb() },
        modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), CircleShape)
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SliderTrack(sliderState: SliderState) {
    Box {
        SliderDefaults.Track(
            sliderState = sliderState,
            thumbTrackGapSize = 0.dp,
            colors = SliderDefaults.colors(
                activeTrackColor = Color.Gray.copy(alpha = 0.4f),
                inactiveTrackColor = SecondaryDark,
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .offset(y = (-22).dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf(1, 5, 10).forEach { speed ->
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .padding(horizontal = 4.dp)
                ) {
                    Text(text = "${speed}x", style = Typography.bodyMedium, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun SliderThumb() {
    Icon(
        painter = painterResource(R.drawable.media3_icon_playback_speed),
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(32.dp)
    )
}