package com.emc.moodmingle.ui.music

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import coil.compose.rememberAsyncImagePainter
import com.emc.moodmingle.R
import com.emc.moodmingle.api.soundcloud.model.TrackResponse
import com.emc.moodmingle.api.soundcloud.viewmodel.SearchViewModel
import com.emc.moodmingle.ui.create.formatDuration
import com.emc.moodmingle.ui.create.post.CreatePostDialogHeader
import com.emc.moodmingle.ui.settings.saved.utils.EmptyComponent
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.CenteredLoadingIndicator
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun MusicPicker(onMusicSelected: (TrackResponse) -> Unit, onDismiss: () -> Unit) {
    val viewModel = hiltViewModel<SearchViewModel>()

    val tracks by viewModel.tracks.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    var query by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.Black,
        topBar = { CreatePostDialogHeader(label = "Find Music") { onDismiss() } }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SearchField(query, viewModel) { query = it }
                    SearchIcon(query, viewModel)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                loading -> CenteredLoadingIndicator()

                error -> EmptyComponent(
                    iconRes = R.drawable.no_search,
                    text = "Failed to get music. Please try again or check your internet connection."
                )

                else -> MusicItems(tracks, viewModel, onMusicSelected, onDismiss)
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    viewModel: SearchViewModel,
    onSearchChanged: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = query,
        onValueChange = onSearchChanged,
        placeholder = { Text(text = "Search music...") },
        shape = CircleShape,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedPlaceholderColor = GrayTextColor,
            unfocusedBorderColor = Color.Transparent,
            unfocusedContainerColor = SecondaryDark,
            focusedContainerColor = SecondaryDark,
            focusedTextColor = Color.White,
            focusedBorderColor = Color.Transparent,
            cursorColor = Color.White
        ),
        trailingIcon = {
            if (query.isNotBlank()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Search",
                    tint = Color.White,
                    modifier = Modifier.clickable { onSearchChanged("") }
                )
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = if (query.isBlank()) ImeAction.Done else ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                viewModel.searchTracks(query)
                focusManager.clearFocus()
            },
            onDone = { focusManager.clearFocus() },
        )
    )
}

@Composable
private fun SearchIcon(query: String, viewModel: SearchViewModel) {
    Box(
        modifier = Modifier
            .clickable(enabled = query.isNotBlank()) {
                viewModel.searchTracks(query)
            }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun MusicItems(
    tracks: List<TrackResponse>,
    viewModel: SearchViewModel,
    onMusicSelected: (TrackResponse) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val mediaPlayers = remember { mutableStateMapOf<String, MediaPlayer>() }
    var currentlyPlaying by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) { onDispose { mediaPlayers.values.forEach { it.release() } } }

    LaunchedEffect(mediaPlayers[currentlyPlaying]) {
        currentlyPlaying?.let { prev ->
            if (mediaPlayers[prev]?.currentPosition == mediaPlayers[prev]?.duration) {
                mediaPlayers[prev]?.stop()
            }
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = tracks, key = { it.id }) { track ->
            Log.d("MusicItem", "Duration: ${track.duration}")

            var playableUrl by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                playableUrl = viewModel.getPlayableUrlFromServer(track.id)
            }

            TrackItem(
                context,
                track,
                playableUrl,
                currentlyPlaying,
                mediaPlayers,
                onMusicSelected,
                onPlayingChanged = { currentlyPlaying = it },
                onDismiss
            )
        }
    }
}

@Composable
private fun LazyItemScope.TrackItem(
    context: Context,
    track: TrackResponse,
    playableUrl: String?,
    currentlyPlaying: String?,
    mediaPlayers: SnapshotStateMap<String, MediaPlayer>,
    onMusicSelected: (TrackResponse) -> Unit,
    onPlayingChanged: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .clickable { onMusicSelected(track); onDismiss() }
            .padding(8.dp)
            .fillMaxWidth()
            .animateItem()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MusicArtwork(track)

            Column(modifier = Modifier.width(240.dp)) {
                MusicTitleAndArtist(track)
                MusicDuration(track)
            }
        }

        PlayOrPauseIcon(
            context,
            playableUrl,
            currentlyPlaying,
            mediaPlayers,
            onPlayingChanged
        )
    }
}

@Composable
private fun MusicArtwork(track: TrackResponse) {
    Image(
        painter = rememberAsyncImagePainter(track.artworkUrl),
        contentDescription = track.title,
        modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun MusicTitleAndArtist(track: TrackResponse) {
    Text(
        text = track.title,
        maxLines = 1,
        color = Color.White,
        overflow = TextOverflow.Ellipsis
    )

    Text(text = track.artist, maxLines = 1, style = Typography.bodyMedium)
}

@Composable
private fun MusicDuration(track: TrackResponse) {
    Text(
        text = formatDuration(track.duration),
        style = Typography.bodyMedium.copy(color = GrayTextColor),
    )
}

@Composable
private fun PlayOrPauseIcon(
    context: Context,
    url: String?,
    currentlyPlaying: String?,
    mediaPlayers: SnapshotStateMap<String, MediaPlayer>,
    onPlayingChanged: (String?) -> Unit
) {
    if (url == null) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(28.dp)
                .drawGradient(),
            strokeWidth = 1.dp
        )
    } else {
        val isPlaying = currentlyPlaying == url

        Icon(
            painter = painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
            contentDescription = "Play/Pause",
            tint = Color.White,
            modifier = Modifier
                .size(28.dp)
                .clickable {
                    if (isPlaying) {
                        mediaPlayers[url]?.pause()
                        onPlayingChanged(null)
                    } else {
                        currentlyPlaying?.let { prev -> mediaPlayers[prev]?.pause() }

                        val mp = mediaPlayers.getOrPut(url) {
                            MediaPlayer.create(context, url.toUri())
                        }
                        mp.start()
                        onPlayingChanged(url)
                    }
                }
        )
    }
}