package com.emc.moodmingle.ui.create

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.LruCache
import android.util.Size
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.scale
import androidx.media3.common.util.UnstableApi
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.modifier.drawGradient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@OptIn(UnstableApi::class)
@Composable
fun VideoGallery(
    uris: List<Uri>,
    selectMultiple: Boolean = true,
    minSize: Dp = 120.dp,
    onUrisSelected: (List<Uri>) -> Unit
) {
    val context = LocalContext.current
    var allVideoUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val gridState = rememberLazyGridState()
    var isLoadingMore by remember { mutableStateOf(true) }
    var pageSize by remember { mutableIntStateOf(35) }

    val thumbnailCache = remember { LruCache<Uri, Bitmap>(50) }

    val permissionLauncher = rememberMediaPermissionLauncher(
        onGranted = {},
        onDenied = { isLoadingMore = false }
    )

    LaunchedEffect(Unit) {
        handlePermissionAndLoad(
            context = context,
            permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                android.Manifest.permission.READ_MEDIA_VIDEO
            else android.Manifest.permission.READ_EXTERNAL_STORAGE,
            permissionLauncher = permissionLauncher,
            loader = { loadDeviceVideos(context) },
            onLoaded = { videos ->
                allVideoUris = videos
                isLoadingMore = false
            },
            onLoadingState = { isLoadingMore = it }
        )
    }

    LaunchedEffect(gridState, allVideoUris, pageSize) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null &&
                    lastVisibleIndex >= pageSize - 3 &&
                    !isLoadingMore &&
                    pageSize < allVideoUris.size
                ) {
                    isLoadingMore = true
                    delay(300)
                    pageSize += 35
                    isLoadingMore = false
                }
            }
    }

    val pagedImageUris = allVideoUris.take(pageSize)

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Adaptive(minSize = minSize),
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryDark),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(pagedImageUris, key = { it.toString() }) { uri ->
            val isSelected = uris.contains(uri)

            var thumbnail by remember { mutableStateOf(thumbnailCache[uri]) }

            // Load thumbnail in background if not cached
            LaunchedEffect(uri) {
                if (thumbnail == null) {
                    val bmp = withContext(Dispatchers.IO) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                context.contentResolver.loadThumbnail(uri, Size(1280, 720), null)
                            } else {
                                retrieveVideoFrame(context, uri)?.scale(1280, 720)
                            }
                        } catch (_: Exception) {
                            null
                        }
                    }
                    if (bmp != null) {
                        thumbnailCache.put(uri, bmp)
                        thumbnail = bmp
                    }
                }
            }

            // Load duration asynchronously
            var durationText by remember { mutableStateOf("") }
            LaunchedEffect(uri) {
                withContext(Dispatchers.IO) {
                    try {
                        val retriever = android.media.MediaMetadataRetriever()
                        retriever.setDataSource(context, uri)
                        val durationMs = retriever.extractMetadata(
                            android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
                        )?.toLongOrNull() ?: 0L
                        durationText =
                            "%02d:%02d".format(durationMs / 1000 / 60, (durationMs / 1000) % 60)
                        retriever.release()
                    } catch (_: Exception) {
                    }
                }
            }

            Box(
                modifier = Modifier
                    .height(minSize)
                    .clickable {
                        if (selectMultiple) {
                            val newSelection = if (isSelected) uris - uri else uris + uri
                            onUrisSelected(newSelection)
                        } else {
                            onUrisSelected(if (isSelected) emptyList() else listOf(uri))
                        }
                    }
            ) {
                if (thumbnail != null) {
                    Image(
                        bitmap = thumbnail!!.asImageBitmap(),
                        contentDescription = "Video thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .drawGradient(),
                            strokeWidth = 2.dp
                        )
                    }
                }

                // Display duration
                Text(
                    text = durationText,
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .align(Alignment.BottomEnd)
                )

                // Selected overlay
                AnimatedVisibility(
                    visible = isSelected,
                    enter = fadeIn(animationSpec = tween(200)),
                    exit = fadeOut(animationSpec = tween(200))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.45f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Selected",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp), strokeWidth = 4.dp)
    }
}

// Helper for older devices
fun retrieveVideoFrame(context: android.content.Context, uri: Uri): Bitmap? {
    return try {
        val retriever = android.media.MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        retriever.getFrameAtTime(0, android.media.MediaMetadataRetriever.OPTION_CLOSEST)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


// Format remaining duration
fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}


/*
@OptIn(UnstableApi::class)
@Composable
fun VideoGalleryWithPlayer(selectedVideos: List<Uri>, onSelectedVideo: (List<Uri>) -> Unit
) {
    val context = LocalContext.current
    var videoUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var playingStates by remember { mutableStateOf<Map<Uri, Boolean>>(emptyMap()) }
    val players = remember { mutableMapOf<Uri, ExoPlayer>() }

    val permissionLauncher = rememberMediaPermissionLauncher(
        onGranted = { },
        onDenied = { isLoading = false }
    )

    LaunchedEffect(Unit) {
        handlePermissionAndLoad(
            context = context,
            permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                android.Manifest.permission.READ_MEDIA_VIDEO
            else android.Manifest.permission.READ_EXTERNAL_STORAGE,
            permissionLauncher = permissionLauncher,
            loader = { loadDeviceVideos(context) },
            onLoaded = {
                videoUris = it
                isLoading = false
            },
            onLoadingState = { isLoading = it }
        )
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(BrushPrimaryGradient, alpha = 0.7f)
                .padding(top = 4.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(videoUris) { uri ->
                val isSelected = selectedVideos.contains(uri)
                var remainingTime by remember { mutableLongStateOf(0L) }

                val exoPlayer = remember(uri) {
                    ExoPlayer.Builder(context).build().apply {
                        setMediaItem(MediaItem.fromUri(uri))
                        prepare()
                    }.also { players[uri] = it } // SAVE PLAYER IN MAP
                }

                val listener = object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        remainingTime =
                            (exoPlayer.duration - exoPlayer.currentPosition).coerceAtLeast(0L)
                        if (playbackState == Player.STATE_ENDED) {
                            exoPlayer.pause()
                            exoPlayer.seekTo(0)
                            remainingTime = exoPlayer.duration
                        }
                    }
                }

                LaunchedEffect(exoPlayer) {
                    exoPlayer.addListener(listener)
                }
                DisposableEffect(exoPlayer) {
                    onDispose {
                        exoPlayer.removeListener(listener)
                        exoPlayer.release()
                    }
                }

                LaunchedEffect(playingStates[uri]) {
                    while (playingStates[uri] == true) {
                        remainingTime =
                            (exoPlayer.duration - exoPlayer.currentPosition).coerceAtLeast(0L)
                        kotlinx.coroutines.delay(200) // update every 200ms
                    }
                }

                Box(
                    modifier = Modifier
                        .background(Color.Black, RoundedCornerShape(8.dp))
                        .height(200.dp)
                        .clickable {
                            val newSelection = if (isSelected) {
                                selectedVideos - uri
                            } else {
                                selectedVideos + uri
                            }
                            onSelectedVideo(newSelection)
                        }
                ) {
                    AndroidView(
                        factory = {
                            PlayerView(it).apply {
                                player = exoPlayer
                                useController = false
                                layoutParams = android.view.ViewGroup.LayoutParams(
                                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = formatDuration(remainingTime),
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .background(
                                    Color.Black.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .align(Alignment.BottomStart)
                        )

                        Icon(
                            painter = painterResource(
                                if (playingStates[uri] == true) R.drawable.pause else R.drawable.play
                            ),
                            contentDescription = "Play/Pause",
                            tint = Color.White,
                            modifier = Modifier
                                .size(24.dp)
                                .offset(y = (-4).dp)
                                .align(Alignment.BottomEnd)
                                .clickable {
                                    // PAUSE ANY PREVIOUSLY PLAYING VIDEO
                                    val currentlyPlayingUri =
                                        playingStates.entries.find { it.value }?.key
                                    if (currentlyPlayingUri != null && currentlyPlayingUri != uri) {
                                        players[currentlyPlayingUri]?.pause()
                                        playingStates = playingStates.toMutableMap()
                                            .apply { put(currentlyPlayingUri, false) }
                                    }

                                    // TOGGLE THIS VIDEO
                                    if (playingStates[uri] == true) {
                                        exoPlayer.pause()
                                        playingStates =
                                            playingStates.toMutableMap().apply { put(uri, false) }
                                    } else {
                                        exoPlayer.play()
                                        playingStates =
                                            playingStates.toMutableMap().apply { put(uri, true) }
                                    }
                                }
                        )
                    }


                    AnimatedVisibility(
                        visible = isSelected,
                        enter = fadeIn(animationSpec = tween(200)),
                        exit = fadeOut(animationSpec = tween(200))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.45f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.graphicsLayer(alpha = 0.99f)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
*/
