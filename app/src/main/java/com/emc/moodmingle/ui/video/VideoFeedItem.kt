package com.emc.moodmingle.ui.video

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.ui.create.formatDuration
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.utils.network.NetworkStatus
import com.emc.moodmingle.utils.network.NetworkUtils
import com.emc.moodmingle.utils.modifier.drawGradient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun VideoFeedItem(
    exoPlayer: ExoPlayer,
    videoUrl: String,
    post: PostEntityFirebase,
    isPageActive: Boolean,
    modifier: Modifier = Modifier,
    onSelectedVideoUrl: (String) -> Unit,
    onShowCommentBottomSheet: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val networkUtils = remember { NetworkUtils(context) }
    var networkStatus by remember { mutableStateOf<NetworkStatus>(NetworkStatus.Connected) }

    var longPressed by remember { mutableStateOf(false) }
    var paused by rememberSaveable { mutableStateOf(false) }
    var showPauseIcon by rememberSaveable { mutableStateOf(false) }
    var muted by rememberSaveable { mutableStateOf(false) }

    var isSeeking by remember { mutableStateOf(false) }
    var seekPosition by remember { mutableFloatStateOf(0f) }

    val lifecycleOwner = LocalLifecycleOwner.current

    var duration by remember { mutableLongStateOf(0L) }
    var position by remember { mutableLongStateOf(0L) }

    /* play / pause */
    LaunchedEffect(isPageActive, networkStatus) {
        if (isPageActive && networkStatus is NetworkStatus.Connected) {
            exoPlayer.playWhenReady = true
        } else {
            exoPlayer.pause()
        }
    }

    /* lifecycle handling */
    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) = exoPlayer.pause()

            override fun onResume(owner: LifecycleOwner) {
                if (isPageActive && networkStatus is NetworkStatus.Connected) {
                    exoPlayer.playWhenReady = true
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(exoPlayer) {
        while (isActive) {
            if (!isSeeking) {
                duration = exoPlayer.duration.coerceAtLeast(0)
                position = exoPlayer.currentPosition
            }
            delay(300)
        }
    }

    /* network monitoring */
    LaunchedEffect(Unit) {
        networkUtils.observeNetworkChanges().collectLatest { networkStatus = it }
    }

    LaunchedEffect(showPauseIcon) {
        if (showPauseIcon) {
            delay(1000)
            showPauseIcon = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(480.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (paused) {
                            exoPlayer.playWhenReady = true
                            paused = false

                            showPauseIcon = true
                        } else {
                            exoPlayer.pause()
                            paused = true
                        }
                    },
                    onLongPress = { longPressed = !longPressed }
                )
            }
    ) {
        when (networkStatus) {
            is NetworkStatus.NoInternet,
            is NetworkStatus.SlowInternet -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                retryConnection(scope, networkUtils) { networkStatus = it }
            }

            is NetworkStatus.Connected -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    AndroidView(
                        factory = {
                            PlayerView(it).apply {
                                player = exoPlayer
                                useController = false
                            }
                        }
                    )

                    MuteIcon(muted, exoPlayer, longPressed, onMuted = { muted = it })
                    PauseAndPlayIcon(showPauseIcon, paused)
                }
            }
        }

        AnimatedVisibility(
            visible = !longPressed,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                VideoItemInteractionContent(
                    post,
                    videoUrl,
                    onCommentBottomSheet = {
                        onSelectedVideoUrl(videoUrl)
                        onShowCommentBottomSheet(it)
                    }
                )

                VideoItemUserContent(post)
                VideoItemPostInformation(post)
                VideoDurationProgress(
                    exoPlayer,
                    position,
                    duration,
                    isSeeking,
                    seekPosition,
                    paused,
                    onSeeking = { isSeeking = it },
                    onSeekPosition = { seekPosition = it },
                )
                VideoDuration(position, duration)
            }
        }
    }
}

@Composable
fun BoxScope.MuteIcon(
    muted: Boolean,
    exoPlayer: ExoPlayer,
    longPressed: Boolean,
    onMuted: (Boolean) -> Unit
) {
    AnimatedVisibility(
        visible = !longPressed,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .offset(x = (-16).dp, y = 32.dp)
            .align(Alignment.TopEnd)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                .clickable {
                    onMuted(!muted)
                    exoPlayer.volume = if (!muted) 0f else 1f
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(if (muted) R.drawable.pause_sound else R.drawable.play_sound),
                contentDescription = "Play/Pause",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
fun PauseAndPlayIcon(showPauseIcon: Boolean, paused: Boolean) {
    AnimatedVisibility(
        visible = showPauseIcon,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Icon(
            painter = painterResource(R.drawable.pause),
            contentDescription = "Pause",
            modifier = Modifier
                .size(52.dp)
                .drawGradient()
        )
    }

    if (paused) {
        Icon(
            painter = painterResource(R.drawable.play),
            contentDescription = "Retry",
            modifier = Modifier
                .size(52.dp)
                .drawGradient()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDurationProgress(
    exoPlayer: ExoPlayer,
    position: Long,
    duration: Long,
    isSeeking: Boolean,
    seekPosition: Float,
    paused: Boolean,
    onSeeking: (Boolean) -> Unit,
    onSeekPosition: (Float) -> Unit,
) {
    Slider(
        value = if (duration > 0) (if (isSeeking) seekPosition else position.toFloat()) / duration
        else 0f,
        onValueChange = { value ->
            if (!isSeeking) exoPlayer.pause()
            onSeeking(true)
            onSeekPosition((value * duration))
        },
        onValueChangeFinished = {
            exoPlayer.seekTo(seekPosition.toLong())
            if (!paused) exoPlayer.playWhenReady = true
            onSeeking(false)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp),
        thumb = {},
        colors = SliderDefaults.colors(
            activeTrackColor = Color.White,
            activeTickColor = PurplePrimary,
            inactiveTrackColor = Color.White,
            disabledThumbColor = PurplePrimary
        ),
        track = {
            SliderDefaults.Track(sliderState = it, modifier = Modifier.height(6.dp))
        }
    )
}

fun retryConnection(
    scope: CoroutineScope,
    networkUtils: NetworkUtils,
    onNetworkStatus: (NetworkStatus) -> Unit
) {
    scope.launch {
        delay(1000)
        val networkStatus =
            if (networkUtils.isInternetAvailable()) NetworkStatus.Connected else NetworkStatus.NoInternet
        onNetworkStatus(networkStatus)
    }
}

@Composable
fun VideoDuration(position: Long, duration: Long) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = formatDuration(position), color = Color.White, fontSize = 12.sp)
        Text(text = formatDuration(duration), color = Color.White, fontSize = 12.sp)
    }
}