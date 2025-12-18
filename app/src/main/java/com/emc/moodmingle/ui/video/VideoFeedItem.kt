package com.emc.moodmingle.ui.video

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.emc.moodmingle.data.firebase.model.PostEntityFirebase
import com.emc.moodmingle.ui.create.formatDuration
import com.emc.moodmingle.utils.NetworkStatus
import com.emc.moodmingle.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun VideoFeedItem(
    videoUrl: String,
    post: PostEntityFirebase,
    isPageActive: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val networkUtils = remember { NetworkUtils(context) }
    var networkStatus by remember { mutableStateOf<NetworkStatus>(NetworkStatus.Connected) }

    val lifecycleOwner = LocalLifecycleOwner.current

    val exoPlayer = rememberOptimizedExoPlayer(context, videoUrl)

    var duration by remember { mutableLongStateOf(0L) }
    var position by remember { mutableLongStateOf(0L) }

    LaunchedEffect(isPageActive, networkStatus) {
        if (isPageActive && networkStatus is NetworkStatus.Connected) {
            exoPlayer.playWhenReady = true
        } else {
            exoPlayer.playWhenReady = false
            exoPlayer.pause()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {

            override fun onPause(owner: LifecycleOwner) {
                exoPlayer.playWhenReady = false
                exoPlayer.pause()
            }

            override fun onResume(owner: LifecycleOwner) {
                if (isPageActive && networkStatus is NetworkStatus.Connected) {
                    exoPlayer.playWhenReady = true
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }

    LaunchedEffect(exoPlayer) {
        while (isActive) {
            duration = exoPlayer.duration.coerceAtLeast(0)
            position = exoPlayer.currentPosition
            delay(300)
        }
    }

    LaunchedEffect(Unit) {
        networkUtils.observeNetworkChanges().collectLatest {
            networkStatus = it
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(480.dp)
    ) {

        when (networkStatus) {
            is NetworkStatus.NoInternet,
            is NetworkStatus.SlowInternet -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
                retryConnection(scope, networkUtils, onNetworkStatus = { networkStatus = it })
            }

            is NetworkStatus.Connected -> {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        PlayerView(it).apply {
                            player = exoPlayer
                            useController = false
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        }
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            VideoItemUserContent(post)

            LinearProgressIndicator(
                progress = {
                    if (duration > 0) position.toFloat() / duration.toFloat() else 0f
                },
                modifier = Modifier.fillMaxWidth()
            )

            VideoDuration(position, duration)
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun rememberOptimizedExoPlayer(
    context: Context,
    videoUrl: String
): ExoPlayer {

    return remember(videoUrl) {
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                1_000,
                15_000,
                300,
                1_000
            )
            .build()

        ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .build()
            .apply {
                setMediaItem(MediaItem.fromUri(videoUrl))
                prepare() // prepares immediately
                repeatMode = Player.REPEAT_MODE_ONE
                playWhenReady = false
            }
    }
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
private fun VideoDuration(position: Long, duration: Long) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = formatDuration(position),
            color = Color.White,
            fontSize = 12.sp,
        )

        Text(
            text = formatDuration(duration),
            color = Color.White,
            fontSize = 12.sp,
        )
    }
}