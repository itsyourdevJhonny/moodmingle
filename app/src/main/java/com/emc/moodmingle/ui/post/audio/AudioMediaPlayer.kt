package com.emc.moodmingle.ui.post.audio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.emc.moodmingle.R
import com.emc.moodmingle.service.AudioPlayerService
import com.emc.moodmingle.ui.post.AnimatedSoundWave
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.PurplePrimary

@Composable
fun AudioMediaPlayer(uri: Uri) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableIntStateOf(0) }
    var duration by remember { mutableIntStateOf(0) }
    var isUserSeeking by remember { mutableStateOf(false) }
    var previewTime by remember { mutableLongStateOf(0L) }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                if (intent?.action == "com.emc.moodmingle.AUDIO_PROGRESS_UPDATE") {
                    currentPosition = intent.getIntExtra("currentPosition", 0)
                    duration = intent.getIntExtra("duration", 0)
                    isPlaying = intent.getBooleanExtra("isPlaying", false)
                }
            }
        }
        val filter = IntentFilter("com.emc.moodmingle.AUDIO_PROGRESS_UPDATE")
        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_EXPORTED
        )

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isUserSeeking) {
            Text(
                text = formatTime(previewTime.toInt()),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Slider(
            value = currentPosition.toFloat(),
            valueRange = 0f..duration.toFloat(),
            onValueChange = { newValue ->
                isUserSeeking = true
                currentPosition = newValue.toInt()
                previewTime = newValue.toLong()
            },
            onValueChangeFinished = {
                isUserSeeking = false
                val seekIntent = Intent(context, AudioPlayerService::class.java).apply {
                    action = "SEEK"
                    putExtra("SEEK_TO", currentPosition)
                }
                ContextCompat.startForegroundService(context, seekIntent)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = PurpleDark,
                activeTrackColor = PurplePrimary,
                inactiveTrackColor = Color.Gray
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(currentPosition), fontSize = 12.sp, color = Color.White)
            Text(formatTime(duration), fontSize = 12.sp, color = Color.White)
        }

        AnimatedSoundWave(isPlaying)

        HorizontalDivider(
            modifier = Modifier
                .background(BrushPrimaryGradient)
                .height(1.dp),
            color = Color.Transparent
        )

        IconButton(
            modifier = Modifier.size(64.dp),
            onClick = {
                val intent = Intent(context, AudioPlayerService::class.java).apply {
                    putExtra("AUDIO_URI", uri)
                    action = if (isPlaying) "PAUSE" else "PLAY"
                }
                ContextCompat.startForegroundService(context, intent)
                isPlaying = !isPlaying
            }
        ) {
            Crossfade(targetState = isPlaying) { playing ->
                if (playing) {
                    Icon(
                        painter = painterResource(R.drawable.pause),
                        contentDescription = "Pause",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.play),
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}