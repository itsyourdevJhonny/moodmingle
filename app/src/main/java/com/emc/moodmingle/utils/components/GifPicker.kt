package com.emc.moodmingle.utils.components

import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.emc.moodmingle.api.giphy.GiphyViewModel
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.data.firebase.model.post.dailymood.Gif
import com.emc.moodmingle.data.firebase.model.post.dailymood.GifType

@Composable
fun GifPicker(mood: DailyMoodEntity, onEdited: (DailyMoodEntity) -> Unit, onDismiss: () -> Unit) {
    Scaffold(
        containerColor = Color.Black,
        topBar = { ScaffoldHeader(title = "Select GIF", onBack = onDismiss) }
    ) { paddingValues ->
        Content(paddingValues, mood, onEdited, onDismiss)
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    mood: DailyMoodEntity,
    onEdited: (DailyMoodEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    val viewModel = hiltViewModel<GiphyViewModel>()
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var query by remember { mutableStateOf("") }
    val gridState = rememberLazyGridState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
    ) {
        SearchGifField(query, viewModel) { query = it }

        if (error) ErrorSection()

        // GRID
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(items = items, key = { it.id }) { gif ->
                val video = gif.images.fixedWidth?.mp4
                    ?: gif.images.original?.mp4
                    ?: gif.images.fixedHeight?.mp4

                val image = gif.images.fixedWidth?.url
                    ?: gif.images.original?.url
                    ?: gif.images.fixedHeight?.url

                when {
                    !video.isNullOrEmpty() -> VideoGif(video, mood, onEdited, onDismiss)
                    !image.isNullOrEmpty() -> ImageGif(image, mood, onEdited, onDismiss)
                }
            }

            // LOADING FOOTER
            if (isLoading) {
                item(span = { GridItemSpan(2) }) { LoadingSection() }
            }
        }
    }

    // PREFETCH
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }.collect { lastVisible ->
            if (lastVisible != null) {
                viewModel.prefetchIfNeeded(visibleIndex = lastVisible, totalCount = items.size)
            }
        }
    }
}

@Composable
private fun SearchGifField(
    query: String,
    viewModel: GiphyViewModel,
    onQueryChanged: (String) -> Unit,
) {
    OutlinedTextField(
        value = query,
        onValueChange = {
            onQueryChanged(it)
            viewModel.search(it)
        },
        placeholder = { Text("Search GIFs & Stickers…") },
        singleLine = true,
        shape = CircleShape,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedTextColor = Color.White,
            unfocusedBorderColor = Color.White,
            unfocusedPlaceholderColor = Color.Gray,
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            focusedPlaceholderColor = Color.White,
            focusedContainerColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}

@Composable
private fun ErrorSection() {
    Text(
        text = "Failed to load GIFs 😢",
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
private fun VideoGif(
    video: String,
    mood: DailyMoodEntity,
    onEdited: (DailyMoodEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    VideoGifPlayer(
        videoUrl = video,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onEdited(
                    mood.copy(
                        gif = Gif(
                            url = video,
                            type = GifType.VIDEO,
                            offsetX = 0f,
                            offsetY = 0f
                        )
                    )
                )
                onDismiss()
            }
    )
}

@Composable
private fun ImageGif(
    image: String,
    mood: DailyMoodEntity,
    onEdited: (DailyMoodEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    AsyncImage(
        model = image,
        contentDescription = null,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onEdited(mood.copy(gif = mood.gif.copy(url = image, type = GifType.IMAGE)))
                onDismiss()
            }
    )
}

@Composable
private fun LoadingSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
        content = { CircularProgressIndicator() }
    )
}

@Composable
fun VideoGifPlayer(videoUrl: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ONE
            volume = 0f
        }
    }

    DisposableEffect(videoUrl) { onDispose { exoPlayer.release() } }

    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                useController = false
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = modifier
    )
}