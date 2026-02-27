package com.emc.moodmingle.ui.screens

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.domain.remote.viewmodel.dailymood.DailyMoodViewModel
import com.emc.moodmingle.ui.settings.saved.media.getMime
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.components.Avatar
import com.emc.moodmingle.utils.text.toColor
import com.emc.moodmingle.utils.text.toFontFamily
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel
import kotlinx.coroutines.flow.first
import kotlin.math.roundToInt

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyMoodScreen(userId: String, onBack: () -> Unit) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val dailyMoodViewModel = hiltViewModel<DailyMoodViewModel>()

    val dailyMoods by dailyMoodViewModel.allActiveDailyMoods.collectAsState()
    var selectedUser by remember { mutableStateOf<UserEntityFirebase?>(null) }

    LaunchedEffect(Unit) { dailyMoodViewModel.observeAllActiveDailyMoods() }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        Avatar(
                            model = selectedUser?.avatarUrl.orEmpty(),
                            size = 38.dp
                        )

                        Text(
                            text = selectedUser?.username.orEmpty() + "hdhahhaah jahd ajhd ajhd ajhd ajdh ",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryDark)
            )
        }
    ) { paddingValues ->
        Content(paddingValues, dailyMoods, userId, userViewModel) { selectedUser = it }
    }

}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    dailyMoods: List<DailyMoodEntity>,
    userId: String,
    userViewModel: FirebaseUserViewModel,
    onUserSelected: (UserEntityFirebase) -> Unit,
) {
    val groupedDailyMoods = dailyMoods.groupBy { it.userId }
    val userDailyMoods = groupedDailyMoods[userId] ?: emptyList()
    val otherDailyMoods = dailyMoods.filter { it.userId != userId }

    val allMoods = userDailyMoods + otherDailyMoods

    val pagerState = rememberPagerState(pageCount = { allMoods.size })

    LaunchedEffect(pagerState.currentPage, allMoods) {
        if (allMoods.isNotEmpty() && pagerState.currentPage < allMoods.size) {
            val userId = allMoods[pagerState.currentPage].userId
            val user = userViewModel.getUserByUid(userId).first().getOrNull()

            if (user != null) onUserSelected(user)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) { page ->
        val mood = allMoods[page]
        val text = mood.text

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
        ) {
            if (mood.media.urls.isNotEmpty()) {
                val url = mood.media.urls[0]
                val mimeType = getMime(url)

                when {
                    mimeType.startsWith("image") -> {
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    mimeType.startsWith("video") -> {
                        DailyMoodVideoPlayer(url)
                    }
                }

            }

            Text(
                text = text.description,
                color = text.color.toColor(),
                fontFamily = text.font.toFontFamily(),
                modifier = Modifier.offset {
                    IntOffset(x = text.offsetX.roundToInt(), y = text.offsetY.roundToInt())
                }
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun DailyMoodVideoPlayer(videoUrl: String) {
    val context = LocalContext.current

    var isPlaying by remember { mutableStateOf(true) }

    // Create exoplayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            repeatMode = Player.REPEAT_MODE_ONE
            prepare()
            playWhenReady = true
        }
    }

    // Update state when player changes
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Release player when composable leaves
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Box(contentAlignment = Alignment.Center) {
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = false
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .clickable { if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play() }
        )

        if (!isPlaying) {
            Icon(
                painter = painterResource(androidx.media3.session.R.drawable.media3_icon_pause),
                contentDescription = null
            )
        }
    }
}