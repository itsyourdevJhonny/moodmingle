package com.emc.moodmingle.ui.post.audio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.emc.moodmingle.R
import com.emc.moodmingle.service.AudioPlayerService
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.utils.modifier.drawGradient
import kotlin.random.Random

@Composable
fun AudioMediaPlayer(url: String) {
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


    val scaleAnim by animateFloatAsState(
        targetValue = if (isPlaying) 1.2f else 1f,
        animationSpec = tween(300, easing = EaseInOut)
    )

    val pulseAnim by rememberInfiniteTransition().animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val rotationAnim by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = if (isPlaying) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer {
                    if (isPlaying) {
                        rotationZ = rotationAnim
                        scaleX = pulseAnim
                        scaleY = pulseAnim
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.music_disk),
                contentDescription = "Music Disk",
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )
        }

        SoundWaveAnimated(isPlaying)

        Spacer(modifier = Modifier.height(24.dp))

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
            onValueChange = { newValue ->
                isUserSeeking = true
                currentPosition = newValue.toInt()
                previewTime = newValue.toLong()
            },
            valueRange = 0f..duration.toFloat(),
            onValueChangeFinished = {
                isUserSeeking = false
                val seekIntent = Intent(context, AudioPlayerService::class.java).apply {
                    action = "SEEK"
                    putExtra("SEEK_TO", currentPosition)
                }
                ContextCompat.startForegroundService(context, seekIntent)
            },
            colors = SliderDefaults.colors(
                thumbColor = PurpleDark,
                activeTrackColor = PurplePrimary,
                inactiveTrackColor = Color.Gray
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = formatTime(currentPosition), color = Color.White, fontSize = 12.sp)
            Text(text = formatTime(duration), color = Color.White, fontSize = 12.sp)
        }


        Box(
            modifier = Modifier
                .width(70.dp)
                .height(50.dp)
                .graphicsLayer {
                    scaleX = scaleAnim
                    scaleY = scaleAnim
                }
                .background(
                    Brush.radialGradient(listOf(PurplePrimary, PurpleDark)),
                    shape = CircleShape
                )
                .clickable {
                    val intent = Intent(context, AudioPlayerService::class.java).apply {
                        putExtra("AUDIO_URI", url.toUri())
                        action = if (isPlaying) "PAUSE" else "PLAY"
                    }
                    ContextCompat.startForegroundService(context, intent)
                    isPlaying = !isPlaying
                },
            contentAlignment = Alignment.Center
        ) {
            Crossfade(targetState = isPlaying) { playing ->
                Icon(
                    painter = if (playing) painterResource(R.drawable.pause)
                    else painterResource(R.drawable.play),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun SoundWaveAnimated(isPlaying: Boolean) {
    val barCount = 12
    val animatedHeights = remember { List(barCount) { Animatable(0.3f) } }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                animatedHeights.forEach { anim ->
                    val randomTarget = Random.nextFloat().coerceIn(0.2f, 1f)
                    anim.animateTo(
                        targetValue = randomTarget,
                        animationSpec = tween(Random.nextInt(150, 350))
                    )
                }
            }
        } else {
            animatedHeights.forEach { anim ->
                anim.animateTo(0.2f, tween(200))
            }
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 12.dp)
    ) {
        val barWidth = size.width / (barCount * 2)
        val spacing = barWidth
        val maxHeight = size.height
        for (i in 0 until barCount) {
            val barHeight = maxHeight * animatedHeights[i].value
            drawRoundRect(
                brush = BrushPrimaryGradient,
                topLeft = androidx.compose.ui.geometry.Offset(
                    i * (barWidth + spacing),
                    maxHeight - barHeight
                ),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
            )
        }
    }
}