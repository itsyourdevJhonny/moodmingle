package com.emc.moodmingle.ui.post.audio

import androidx.annotation.OptIn
import androidx.compose.animation.core.EaseInOut
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.utils.modifier.drawGradient
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(UnstableApi::class)
@Composable
fun AudioMediaPlayer(url: String, bpm: Float = 120f) {
    val context = LocalContext.current

    var isPlaying by remember { mutableStateOf(false) }
    var position by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var isSeeking by remember { mutableStateOf(false) }

    val barCount = 12
    val amplitudes = remember { FloatArray(barCount) }
    val seeds = remember { FloatArray(barCount) { Random.nextFloat() * 2f } }

    val exoPlayer = remember {
        val cacheDataSource = CacheDataSource.Factory()
            .setCache(AudioCache.get(context))
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSource))
            .build()
            .apply {
                setMediaItem(MediaItem.fromUri(url))
                prepare()
            }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    // -----------------------------
    // Detect if visible on screen
    // -----------------------------
    var isVisible by remember { mutableStateOf(true) }
    val visibilityThreshold = 0.7f // fraction of height visible to count as visible

    val playerModifier = Modifier.onGloballyPositioned { coords ->
        val windowBounds = coords.windowBounds()
        val heightVisible = (windowBounds.bottom - windowBounds.top)
        isVisible = heightVisible / coords.size.height.toFloat() > visibilityThreshold
    }

    LaunchedEffect(isPlaying, isVisible) {
        if (!isVisible && exoPlayer.isPlaying) {
            exoPlayer.pause()
            isPlaying = false
        }
    }

    // -----------------------------
    // Playback state polling
    // -----------------------------
    LaunchedEffect(Unit) {
        while (true) {
            if (!isSeeking) {
                position = exoPlayer.currentPosition
                duration = exoPlayer.duration.coerceAtLeast(0L)
                isPlaying = exoPlayer.isPlaying
            }
            delay(250)
        }
    }

    // -----------------------------
    // FFT + BPM
    // -----------------------------
    LaunchedEffect(isPlaying) {
        if (!isPlaying) {
            for (i in amplitudes.indices) amplitudes[i] = 0f
            return@LaunchedEffect
        }

        val beatPeriod = (60_000 / bpm).toLong()
        while (isPlaying) {
            val t = exoPlayer.currentPosition / 200.0
            for (i in amplitudes.indices) {
                val freq = 0.5f + i * 0.15f
                val energy = kotlin.math.abs(kotlin.math.sin(t * freq + seeds[i]))
                amplitudes[i] = lerp(amplitudes[i], energy.toFloat(), 0.25f)
            }
            delay(beatPeriod / 8)
        }
    }

    val scaleAnim by animateFloatAsState(
        targetValue = if (isPlaying) 1.2f else 1f,
        animationSpec = tween(300, easing = EaseInOut)
    )

    val rotationAnim by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = if (isPlaying) 360f else 0f,
        animationSpec = infiniteRepeatable(tween(4000), RepeatMode.Restart)
    )

    val pulseAnim by rememberInfiniteTransition().animateFloat(
        initialValue = 0.85f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse)
    )

    // -----------------------------
    // UI
    // -----------------------------
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .then(playerModifier), // attach visibility detector
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer {
                    rotationZ = if (isPlaying) rotationAnim else 0f
                    scaleX = pulseAnim
                    scaleY = pulseAnim
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(R.drawable.music_disk),
                contentDescription = null,
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )
        }

        SoundWaveFFT(amplitudes, isPlaying)

        Slider(
            value = position.toFloat(),
            valueRange = 0f..duration.toFloat(),
            onValueChange = {
                isSeeking = true
                position = it.toLong()
            },
            onValueChangeFinished = {
                exoPlayer.seekTo(position)
                isSeeking = false
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(position.toInt()), color = Color.White, fontSize = 12.sp)
            Text(formatTime(duration.toInt()), color = Color.White, fontSize = 12.sp)
        }

        Box(
            modifier = Modifier
                .width(70.dp)
                .height(50.dp)
                .graphicsLayer { scaleX = scaleAnim; scaleY = scaleAnim }
                .background(Brush.radialGradient(listOf(PurplePrimary, PurpleDark)), CircleShape)
                .clickable {
                    if (exoPlayer.isPlaying) exoPlayer.pause()
                    else exoPlayer.play()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = if (isPlaying) painterResource(R.drawable.pause)
                else painterResource(R.drawable.play),
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

/** Utility: convert LayoutCoordinates to window bounds */
fun LayoutCoordinates.windowBounds(): Rect {
    val position = this.localToWindow(Offset.Zero)
    return Rect(
        position.x,
        position.y,
        position.x + size.width,
        position.y + size.height
    )
}


@Composable
fun SoundWaveFFT(amplitudes: FloatArray, isPlaying: Boolean) {
    val barCount = amplitudes.size

    Canvas(
        Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 12.dp)
    ) {
        val barWidth = size.width / (barCount * 2)
        val spacing = barWidth
        val maxHeight = size.height

        for (i in 0 until barCount) {
            val amp = if (isPlaying) amplitudes[i] else 0.12f
            val barHeight = maxHeight * (0.15f + amp * 0.85f)

            drawRoundRect(
                brush = BrushPrimaryGradient,
                topLeft = Offset(i * (barWidth + spacing), maxHeight - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(8f)
            )
        }
    }
}

fun lerp(start: Float, end: Float, fraction: Float): Float {
    return start + (end - start) * fraction
}

