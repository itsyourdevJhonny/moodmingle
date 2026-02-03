package com.emc.moodmingle.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.emc.moodmingle.R
import com.emc.moodmingle.api.soundcloud.viewmodel.SearchViewModel
import com.emc.moodmingle.ui.create.post.CreatePostDialogHeader
import com.emc.moodmingle.ui.post.audio.AudioMediaPlayer
import com.emc.moodmingle.ui.settings.saved.utils.EmptyComponent
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark

@Composable
fun SearchMusicScreen() {
    val viewModel = hiltViewModel<SearchViewModel>()

    var query by remember { mutableStateOf("") }

    val tracks by viewModel.tracks.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        containerColor = Color.Black,
        topBar = { CreatePostDialogHeader("Find Music") { } }
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
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
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
                                    modifier = Modifier.clickable { query = "" }
                                )
                            }
                        }
                    )

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
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                error -> {
                    EmptyComponent(
                        iconRes = R.drawable.no_search,
                        text = "Failed to get music. Please try again or check your internet connection."
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(tracks) { track ->
                            var playableUrl by remember { mutableStateOf<String?>(null) }

                            Log.d("SearchMusicScreen", "URL: $playableUrl")

                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable { },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(track.artworkUrl),
                                    contentDescription = track.title,
                                    modifier = Modifier
                                        .height(120.dp)
                                        .fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(track.title, maxLines = 1)
                                Text(
                                    text = track.artist,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.bodySmall
                                )

                                LaunchedEffect(Unit) {
                                    playableUrl = viewModel.getPlayableUrlFromServer(track.id)
                                }

                                playableUrl?.let {
                                    AudioMediaPlayer(playableUrl.orEmpty())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

