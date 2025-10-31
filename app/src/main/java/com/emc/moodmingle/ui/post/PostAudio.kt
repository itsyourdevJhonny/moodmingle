package com.emc.moodmingle.ui.post

import androidx.annotation.OptIn
import androidx.annotation.RawRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.post.audio.AudioMediaPlayer
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.PurplePrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.random.Random

@OptIn(UnstableApi::class)
@Composable
fun PostAudio(@RawRes audioRes: Int, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val audioUri = remember(audioRes) {
        "android.resource://${context.packageName}/$audioRes".toUri()
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(audioUri))
            prepare()
        }
    }

    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var totalDuration by remember { mutableLongStateOf(0L) }

    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isUserSeeking by remember { mutableStateOf(false) }

    LaunchedEffect(exoPlayer, isPlaying, isUserSeeking) {
        while (isActive) {
            if (!isUserSeeking) {
                currentPosition = exoPlayer.currentPosition
                totalDuration = exoPlayer.duration.coerceAtLeast(1L)
                sliderPosition = currentPosition / totalDuration.toFloat()
            }
            delay(200L)
        }
    }

    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playWhenReady: Boolean) {
                isPlaying = playWhenReady
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C))
    ) {
        AudioMediaPlayer(uri = audioUri)
    }
}

@Composable
fun AudioSlider(
    exoPlayer: ExoPlayer,
    isUserSeeking: Boolean,
    sliderPosition: Float,
    totalDuration: Long,
    onUserSeeking: (Boolean) -> Unit,
    onSliderPosition: (Float) -> Unit,
    onCurrentPosition: (Long) -> Unit
) {
    var previewTime by remember { mutableLongStateOf(0L) }

    if (isUserSeeking) {
        Text(
            text = formatTime(previewTime),
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }

    Slider(
        value = sliderPosition,
        onValueChange = { value ->
            onUserSeeking(true)
            onSliderPosition(value)
            previewTime = (value * totalDuration).toLong()
        },
        onValueChangeFinished = {
            val seekTo = (sliderPosition * totalDuration).toLong()
            exoPlayer.seekTo(seekTo)
            onCurrentPosition(seekTo)
            onUserSeeking(false)
        },
        colors = SliderDefaults.colors(
            thumbColor = PurpleDark,
            activeTrackColor = PurplePrimary,
            inactiveTrackColor = Color.Gray
        )
    )
}

@Composable
fun ControllerButton(isPlaying: Boolean, exoPlayer: ExoPlayer) {
    IconButton(
        onClick = {
            if (isPlaying) exoPlayer.pause() else exoPlayer.play()
        },
        modifier = Modifier.size(64.dp),
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

@Composable
fun AnimatedSoundWave(isPlaying: Boolean) {
    val barCount = 10
    val animatedHeights = remember { List(barCount) { Animatable(0.3f) } }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                animatedHeights.forEach { anim ->
                    val randomTarget = Random.nextFloat().coerceIn(0.3f, 1f)
                    anim.animateTo(
                        targetValue = randomTarget,
                        animationSpec = tween(
                            durationMillis = Random.nextInt(200, 300),
                            easing = LinearEasing
                        )
                    )
                }
            }
        } else {
            animatedHeights.forEach { anim ->
                anim.animateTo(
                    targetValue = 0.2f,
                    animationSpec = tween(durationMillis = 200)
                )
            }
        }
    }

    SoundBar(barCount, animatedHeights)
}

@Composable
fun SoundBar(barCount: Int, animatedHeights: List<Animatable<Float, AnimationVector1D>>) {
    Column(
        modifier = Modifier
            .height(60.dp)
            .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)) {
            val barWidth = size.width / (barCount * 2)
            val spacing = barWidth
            val maxHeight = size.height

            for (i in 0 until barCount) {
                val barHeight = maxHeight * animatedHeights[i].value
                val x = i * (barWidth + spacing)
                drawRoundRect(
                    color = PurpleDark,
                    topLeft = androidx.compose.ui.geometry.Offset(x, maxHeight - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(8f, 8f)
                )
            }
        }
    }
}

fun formatTime(ms: Long): String {
    val totalSec = (ms / 1000).toInt()
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%d:%02d".format(min, sec)
}
