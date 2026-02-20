package com.emc.moodmingle.ui.create.util

import android.Manifest
import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.post.action.formatText
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.modifier.drawGradient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AudioGallery(selectedAudios: List<Uri>, onSelectedAudio: (List<Uri>) -> Unit) {
    val context = LocalContext.current
    var allAudios by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var displayedAudios by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val pageSize = 10

    val mediaPlayers = remember { mutableStateMapOf<Uri, MediaPlayer>() }
    val playingState = remember { mutableStateMapOf<Uri, Boolean>() }
    var currentlyPlaying by remember { mutableStateOf<Uri?>(null) }
    val durations = remember { mutableStateMapOf<Uri, Long>() }

    val permissionLauncher = rememberMediaPermissionLauncher(
        onGranted = {},
        onDenied = { isLoading = false }
    )

    // Load all audios and durations asynchronously
    LaunchedEffect(Unit) {
        handlePermissionAndLoad(
            context = context,
            permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE,
            permissionLauncher = permissionLauncher,
            loader = { loadDeviceAudio(context) },
            onLoaded = { audios ->
                allAudios = audios
                displayedAudios = audios.take(pageSize)

                // Preload durations asynchronously
                CoroutineScope(Dispatchers.IO).launch {
                    val retriever = MediaMetadataRetriever()
                    audios.forEach { uri ->
                        try {
                            retriever.setDataSource(context, uri)
                            durations[uri] = retriever.extractMetadata(
                                MediaMetadataRetriever.METADATA_KEY_DURATION
                            )?.toLong() ?: 0L
                        } catch (_: Exception) {
                            durations[uri] = 0L
                        }
                    }
                    retriever.release()
                }

                isLoading = false
            },
            onLoadingState = { loading -> isLoading = loading }
        )
    }

    if (isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp), strokeWidth = 4.dp)
            Text("Loading audio...", color = Color.White, modifier = Modifier.padding(top = 8.dp))
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BrushPrimaryGradient, alpha = 0.7f)
            .padding(top = 2.dp, start = 2.dp, end = 2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(displayedAudios.size) { index ->
            val uri = displayedAudios[index]
            val isSelected = selectedAudios.contains(uri)

            var remainingTime by remember { mutableIntStateOf(0) }
            val isPlaying = playingState[uri] == true

            // Lazy initialize MediaPlayer
            val mediaPlayer = mediaPlayers.getOrPut(uri) {
                MediaPlayer.create(context, uri)
            }

            // Update remaining time while playing
            LaunchedEffect(isPlaying) {
                while (isPlaying) {
                    remainingTime = mediaPlayer.currentPosition
                    if (mediaPlayer.currentPosition >= mediaPlayer.duration) {
                        playingState[uri] = false
                        mediaPlayer.pause()
                        mediaPlayer.seekTo(0)
                        remainingTime = 0
                    }
                    delay(200)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(PrimaryDark)
                    .clickable {
                        val newSelection =
                            if (isSelected) selectedAudios - uri else selectedAudios + uri
                        onSelectedAudio(newSelection)
                    }
                    .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.audio),
                            contentDescription = "Audio",
                            modifier = Modifier
                                .size(24.dp)
                                .graphicsLayer(alpha = 0.99f)
                                .drawGradient()
                        )

                        Text(
                            text = formatText(getFileName(context, uri), 18),
                            color = GrayTextColor,
                            maxLines = 1
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.graphicsLayer(alpha = 0.99f)
                            )
                        }
                        IconButton(onClick = {
                            currentlyPlaying?.let { prevUri ->
                                if (prevUri != uri) {
                                    mediaPlayers[prevUri]?.pause()
                                    playingState[prevUri] = false
                                }
                            }

                            if (isPlaying) {
                                mediaPlayer.pause()
                                playingState[uri] = false
                            } else {
                                mediaPlayer.start()
                                playingState[uri] = true
                                currentlyPlaying = uri
                            }
                        }) {
                            Icon(
                                painter = painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatDuration(remainingTime.toLong()),
                        color = Color.White,
                        fontSize = 12.sp
                    )

                    Text(
                        text = formatDuration(durations[uri] ?: 0L),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            // Pagination: load next page when near end
            if (index >= displayedAudios.size - 1 && displayedAudios.size < allAudios.size) {
                val nextPage = allAudios.drop(displayedAudios.size).take(pageSize)
                displayedAudios = displayedAudios + nextPage
            }
        }
    }
}

fun getFileName(context: Context, uri: Uri): String {
    var name: String? = null

    if (uri.scheme == "content") {
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    name = it.getString(index)
                }
            }
        }
    }

    if (name == null) {
        name = uri.path
        val cut = name?.lastIndexOf('/') ?: -1
        if (cut != -1) {
            name = name?.substring(cut + 1)
        }
    }

    return name ?: "unknown_file"
}
