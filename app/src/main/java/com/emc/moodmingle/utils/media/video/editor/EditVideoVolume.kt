package com.emc.moodmingle.utils.media.video.editor

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.session.R
import com.emc.moodmingle.ui.theme.SecondaryDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVideoVolume(state: VideoEditorState, onStateChanged: (VideoEditorState) -> Unit) {
    val isMuted = state.volume == 0.0f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(bottom = 4.dp)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                .padding(vertical = 6.dp, horizontal = 12.dp)
                .animateContentSize()
        ) {
            Text(text = "Volume:", color = Color.White)

            Text(
                text = "%.1f%%".format(state.volume * 100),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            listOf(
                "Minus" to R.drawable.media3_icon_minus,
                "Plus" to R.drawable.media3_icon_plus,
            ).forEach { (label, icon) ->
                IconButton(
                    onClick = {
                        onStateChanged(
                            state.copy(
                                volume = if (label == "Plus") {
                                    minOf(state.volume + 0.1f, 1f)
                                } else {
                                    maxOf(state.volume - 0.1f, 0f)
                                }
                            )
                        )
                    },
                    enabled = if (label == "Plus") state.volume < 1f else state.volume > 0.0f,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.3f),
                        contentColor = Color.White
                    )
                ) {
                    Icon(painter = painterResource(icon), contentDescription = null)
                }
            }
        }

        Slider(
            value = state.volume,
            valueRange = 0.0f..1f,
            onValueChange = { onStateChanged(state.copy(volume = it)) },
            track = { sliderState ->
                SliderDefaults.Track(
                    sliderState = sliderState,
                    thumbTrackGapSize = 0.dp,
                    colors = SliderDefaults.colors(
                        activeTrackColor = Color.Gray.copy(alpha = 0.4f),
                        inactiveTrackColor = SecondaryDark,
                    )
                )
            },
            thumb = {
                Icon(
                    painter = painterResource(if (isMuted) R.drawable.media3_icon_volume_off else R.drawable.media3_icon_volume_up),
                    contentDescription = null,
                    tint = if (state.volume == 0.0f) Color.Red else Color.White,
                    modifier = Modifier.size(28.dp)
                )
            },
            modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), CircleShape)
        )
    }
}