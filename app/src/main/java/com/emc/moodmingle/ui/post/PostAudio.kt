package com.emc.moodmingle.ui.post

import androidx.annotation.OptIn
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.post.audio.AudioMediaPlayer
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.utils.modifier.drawGradient
import kotlin.random.Random

@OptIn(UnstableApi::class)
@Composable
fun PostAudio(url: String) {
//    val audioUri = url.toUri()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
            .background(SecondaryDark),
        contentAlignment = Alignment.Center
    ) {
        AudioMediaPlayer(url = url)

        Box(
            modifier = Modifier.padding(start = 8.dp, top = 4.dp).fillMaxSize()
        ) {
            Icon(
                painter = painterResource(R.drawable.music),
                contentDescription = "Image",
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.TopStart)
                    .graphicsLayer(alpha = 0.8f)
                    .drawGradient()
            )
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
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
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

/*fun formatTime(ms: Long): String {
    val totalSec = (ms / 1000).toInt()
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%d:%02d".format(min, sec)
}*/
