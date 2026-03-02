package com.emc.moodmingle.ui.screens

import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.domain.remote.model.post.dailymood.gif.GifType
import com.emc.moodmingle.domain.remote.model.post.dailymood.media.DailyMoodMediaType
import com.emc.moodmingle.domain.remote.model.post.dailymood.text.TextStyle
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.domain.remote.viewmodel.dailymood.DailyMoodViewModel
import com.emc.moodmingle.ui.create.dailymood.hashtag.DailyMoodHashtagSection
import com.emc.moodmingle.ui.create.dailymood.mention.DailyMoodMentionSection
import com.emc.moodmingle.ui.create.dailymood.mood.DailyMoodMoodSection
import com.emc.moodmingle.ui.create.dailymood.page.rememberMediaPalette
import com.emc.moodmingle.ui.create.util.getMimeType
import com.emc.moodmingle.ui.settings.saved.media.getMime
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.Avatar
import com.emc.moodmingle.utils.text.toColor
import com.emc.moodmingle.utils.text.toFontFamily
import com.emc.moodmingle.utils.text.toTextAlign
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
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = SecondaryDark),
                actions = { Header(onBack, selectedUser) }
            )
        }
    ) { paddingValues ->
        Content(paddingValues, dailyMoods, userId, userViewModel) { selectedUser = it }
    }

}

@Composable
private fun Header(onBack: () -> Unit, selectedUser: UserEntityFirebase?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }

        Avatar(model = selectedUser?.avatarUrl.orEmpty(), size = 38.dp)

        Text(
            text = selectedUser?.username.orEmpty(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 8.dp)
        )
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
    val context = LocalContext.current

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

    HorizontalPager(state = pagerState) { page ->
        val mood = allMoods[page]
        val text = mood.text
        val media = mood.media

        var paletteColors by remember { mutableStateOf(listOf(Color.Black, Color.Black)) }

        if (mood.media.type == DailyMoodMediaType.SINGLE && mood.media.urls.isNotEmpty()) {
            val uri = mood.media.urls.first().toUri()
            val mimeType = getMimeType(context, uri) ?: ""
            val isVideo = mimeType.startsWith("video")

            val (topColor, bottomColor) = rememberMediaPalette(uri, isVideo).value
            paletteColors = listOf(topColor, bottomColor)
        } else {
            paletteColors = listOf(Color.Black, Color.Black)
        }

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Brush.verticalGradient(paletteColors))
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
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        x = media.image.offsetX.roundToInt(),
                                        y = media.image.offsetY.roundToInt()
                                    )
                                }
                        )
                    }

                    mimeType.startsWith("video") -> {
                        DailyMoodVideoPlayer(url)
                    }
                }

            }

            AnimatedVisibility(
                visible = mood.gif.url.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .offset {
                        IntOffset(
                            mood.gif.offsetX.roundToInt(),
                            mood.gif.offsetY.roundToInt()
                        )
                    }
            ) {
                when (mood.gif.type) {
                    GifType.IMAGE -> {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(mood.gif.url)
                                .crossfade(true)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }

                    GifType.VIDEO -> {
                        val context = LocalContext.current
                        val exoPlayer = remember(mood.gif.url) {
                            ExoPlayer.Builder(context)
                                .build()
                                .apply {
                                    setMediaItem(MediaItem.fromUri(mood.gif.url))
                                    prepare()
                                    playWhenReady = true
                                    repeatMode = Player.REPEAT_MODE_ONE
                                    volume = 0f
                                }
                        }

                        DisposableEffect(mood.gif.url) { onDispose { exoPlayer.release() } }

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
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }

            val color = if (text.color.toColor().luminance() < 0.5f) Color.White else Color.Black

            Text(
                text = text.description,
                color = when (text.style) {
                    TextStyle.NORMAL -> text.color.toColor()
                    TextStyle.WITH_BACKGROUND -> color
                    TextStyle.WITHOUT_BACKGROUND -> Color.White
                },
                fontFamily = text.font.toFontFamily(),
                textAlign = text.align.toTextAlign(),
                modifier = Modifier
                    .offset { IntOffset(text.offsetX.roundToInt(), text.offsetY.roundToInt()) }
//                    .widthIn(max = (boxSize.width - 390).dp)
                    .background(
                        color = when (text.style) {
                            TextStyle.NORMAL -> Color.Transparent
                            TextStyle.WITH_BACKGROUND -> text.color.toColor()
                            TextStyle.WITHOUT_BACKGROUND -> text.color.toColor().copy(alpha = 0.3f)
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            )

            Box(modifier = Modifier.padding(12.dp)) {
                DailyMoodMoodSection(mood)
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 8.dp)
            ) {
                DailyMoodMentionSection(mood) {}
                DailyMoodHashtagSection(mood)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.location),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        text = mood.location?.displayName.orEmpty(),
                        style = Typography.bodyMedium.copy(color = Color.White)
                    )

                }
            }
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