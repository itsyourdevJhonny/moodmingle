package com.emc.moodmingle.ui.video.comment.media.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import com.emc.moodmingle.ui.create.util.detectLongPress
import com.emc.moodmingle.ui.post.PostAudio
import com.emc.moodmingle.ui.settings.saved.media.getMime
import com.emc.moodmingle.ui.video.MuteIcon
import com.emc.moodmingle.ui.video.PauseAndPlayIcon
import com.emc.moodmingle.ui.video.VideoDuration
import com.emc.moodmingle.ui.video.VideoDurationProgress
import com.emc.moodmingle.ui.video.retryConnection
import com.emc.moodmingle.utils.network.NetworkStatus
import com.emc.moodmingle.utils.network.NetworkUtils
import com.emc.moodmingle.utils.exoplayer.ExoPlayerPool
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive

@Composable
fun VideoCommentMediaDialogContent(mediaUrls: List<String>) {
    val context = LocalContext.current
    val playerPool = remember { ExoPlayerPool(context) }

    var pageSize by rememberSaveable { mutableIntStateOf(10) }
    val visibleItems by remember(mediaUrls, pageSize) {
        derivedStateOf { mediaUrls.take(pageSize) }
    }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { visibleItems.size })

    LaunchedEffect(pagerState.currentPage, visibleItems.size) {
        if (pagerState.currentPage >= visibleItems.size - 3 && pageSize < mediaUrls.size) pageSize += 10
    }

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        beyondViewportPageCount = 1,
        key = { visibleItems[it] }
    ) { page ->
        val url = visibleItems[page]
        val mime = getMime(url)

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when {
                mime.startsWith("image") -> DisplayImage(url)
                mime.startsWith("video") -> DisplayVideo(url, playerPool)
                else -> PostAudio(url)
            }
        }
    }
}

@Composable
private fun DisplayImage(url: String) {
    Image(
        painter = rememberAsyncImagePainter(url),
        contentDescription = null,
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun DisplayVideo(url: String, playerPool: ExoPlayerPool) {
    val exoPlayer = remember(url) { playerPool.acquire(url) }

    DisposableEffect(Unit) { onDispose { playerPool.release(exoPlayer) } }

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

    LaunchedEffect(networkStatus) {
        if (networkStatus is NetworkStatus.Connected) {
            exoPlayer.playWhenReady = true
        } else {
            exoPlayer.pause()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) = exoPlayer.pause()

            override fun onResume(owner: LifecycleOwner) {
                if (networkStatus is NetworkStatus.Connected) {
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
        modifier = Modifier
            .fillMaxSize()
            .detectLongPress(
                onLongPress = { longPressed = !longPressed },
                onTap = {
                    if (paused) {
                        exoPlayer.playWhenReady = true
                        paused = false

                        showPauseIcon = true
                    } else {
                        exoPlayer.pause()
                        paused = true
                    }
                }
            )
    ) {
        when (networkStatus) {
            is NetworkStatus.NoInternet,
            is NetworkStatus.SlowInternet -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                retryConnection(scope, networkUtils) { networkStatus = it }
            }

            is NetworkStatus.Connected -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    AndroidView(
                        factory = {
                            PlayerView(it).apply { player = exoPlayer; useController = false }
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
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
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