package com.emc.moodmingle.ui.screens

import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.emc.moodmingle.domain.remote.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.domain.remote.model.post.dailymood.gif.Gif
import com.emc.moodmingle.domain.remote.model.post.dailymood.gif.GifType
import com.emc.moodmingle.domain.remote.model.post.dailymood.media.DailyMoodMedia
import com.emc.moodmingle.domain.remote.model.post.dailymood.media.DailyMoodMediaType
import com.emc.moodmingle.domain.remote.model.post.dailymood.text.TextStyle
import com.emc.moodmingle.domain.remote.viewmodel.dailymood.DailyMoodViewModel
import com.emc.moodmingle.ui.create.dailymood.hashtag.DailyMoodHashtagSection
import com.emc.moodmingle.ui.create.dailymood.location.DailyMoodLocationSection
import com.emc.moodmingle.ui.create.dailymood.mention.DailyMoodMentionSection
import com.emc.moodmingle.ui.create.dailymood.mood.DailyMoodMoodSection
import com.emc.moodmingle.ui.create.dailymood.page.rememberMediaPalette
import com.emc.moodmingle.ui.create.util.getMimeType
import com.emc.moodmingle.ui.settings.saved.media.getMime
import com.emc.moodmingle.utils.text.toColor
import com.emc.moodmingle.utils.text.toFontFamily
import com.emc.moodmingle.utils.text.toTextAlign
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.plus
import kotlin.math.roundToInt

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyMoodScreen(userId: String, onBack: () -> Unit) {
    val dailyMoodViewModel = hiltViewModel<DailyMoodViewModel>()
    val dailyMoods by dailyMoodViewModel.allActiveDailyMoods.collectAsState()

    LaunchedEffect(Unit) { dailyMoodViewModel.observeAllActiveDailyMoods() }

    BackHandler { onBack() }

    Scaffold { paddingValues -> Content(paddingValues, dailyMoods, userId) }
}

@Composable
private fun Content(paddingValues: PaddingValues, dailyMoods: List<DailyMoodEntity>, userId: String) {
    val coroutineScope = rememberCoroutineScope()

    val groupedMoods = dailyMoods
        .groupBy { it.userId }
        .toList()

    val pagerState = rememberPagerState(pageCount = { groupedMoods.size })

    if (groupedMoods.isNotEmpty()) {
        StoryEngine(
            paddingValues = paddingValues,
            moods = dailyMoods,
            userId = userId,
            onFinished = {
                // move to next user automatically
                if (pagerState.currentPage < groupedMoods.lastIndex) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            }
        )
    }
}

@Composable
private fun MoodContent(
    paddingValues: PaddingValues,
    dailyMoods: List<DailyMoodEntity>,
    userId: String,
) {
    val context = LocalContext.current
    val groupedDailyMoods = dailyMoods.groupBy { it.userId }
    val userDailyMoods = groupedDailyMoods[userId] ?: emptyList()
    val otherDailyMoods = dailyMoods.filter { it.userId != userId }

    val allMoods = userDailyMoods + otherDailyMoods

    val pagerState = rememberPagerState(pageCount = { allMoods.size })

    HorizontalPager(state = pagerState) { page ->
        val mood = allMoods[page]

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
            MediaSection(mood)
            GifSection(mood)
            DescriptionSection(mood)
            MoodSection(mood)

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 8.dp)
            ) {
                DailyMoodMentionSection(mood) {}
                DailyMoodHashtagSection(mood)
                DailyMoodLocationSection(mood)
            }
        }
    }
}

@Composable
private fun StoryEngine(
    paddingValues: PaddingValues,
    moods: List<DailyMoodEntity>,
    userId: String,
    onFinished: () -> Unit,
) {
    var currentIndex by remember { mutableIntStateOf(0) }

    // auto progress timer
    LaunchedEffect(currentIndex) {
        delay(10000) // 5 seconds per story
        if (currentIndex < moods.lastIndex) currentIndex++ else onFinished()
    }

    val mood = moods[currentIndex]

    Box(modifier = Modifier.fillMaxSize()) {
        StoryProgressBar(total = moods.size, currentIndex = currentIndex)

        MoodContent(paddingValues, dailyMoods = listOf(mood), userId = userId)

        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { if (currentIndex > 0) currentIndex-- }
                    )
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { if (currentIndex < moods.lastIndex) currentIndex++ else onFinished() }
                    )
            )
        }
    }
}

@Composable
private fun StoryProgressBar(total: Int, currentIndex: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(total) { index ->
            val progress by animateFloatAsState(
                targetValue = when {
                    index < currentIndex -> 1f
                    index == currentIndex -> 1f
                    else -> 0f
                },
                animationSpec = tween(durationMillis = 5000),
                label = ""
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(Color.White)
                )
            }
        }
    }
}

@Composable
private fun BoxScope.MoodSection(mood: DailyMoodEntity) {
    Box(
        modifier = Modifier
            .padding(12.dp)
            .align(Alignment.BottomEnd)
            .offset(y = (-48).dp),
        content = { DailyMoodMoodSection(mood) }
    )
}

@Composable
private fun MediaSection(mood: DailyMoodEntity) {
    val media = mood.media

    if (media.urls.isNotEmpty()) {
        val url = media.urls[0]
        val mimeType = getMime(url)

        when {
            mimeType.startsWith("image") -> Image(url, media)
            mimeType.startsWith("video") -> Video(url)
        }
    }
}

@Composable
private fun Image(url: String, media: DailyMoodMedia) {
    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.offset {
            IntOffset(
                x = media.image.offsetX.roundToInt(),
                y = media.image.offsetY.roundToInt()
            )
        }
    )
}

@OptIn(UnstableApi::class)
@Composable
private fun Video(url: String) {
    val context = LocalContext.current

    var isPlaying by remember { mutableStateOf(true) }

    // Create exoplayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(url)
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

@Composable
private fun GifSection(mood: DailyMoodEntity) {
    val gif = mood.gif
    val gifUrl = gif.url

    AnimatedVisibility(
        visible = gifUrl.isNotEmpty(),
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.offset { IntOffset(gif.offsetX.roundToInt(), gif.offsetY.roundToInt()) }
    ) {
        when (gif.type) {
            GifType.IMAGE -> {
                ImageGif(gif)
            }

            GifType.VIDEO -> {
                VideoGif(gifUrl)
            }
        }
    }
}

@Composable
private fun ImageGif(gif: Gif) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(gif.url)
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

@Composable
private fun VideoGif(gifUrl: String) {
    val context = LocalContext.current
    val exoPlayer = remember(gifUrl) {
        ExoPlayer.Builder(context)
            .build()
            .apply {
                setMediaItem(MediaItem.fromUri(gifUrl))
                prepare()
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_ONE
                volume = 0f
            }
    }

    DisposableEffect(Unit) { onDispose { exoPlayer.release() } }

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

@Composable
private fun DescriptionSection(mood: DailyMoodEntity) {
    val text = mood.text
    val textColor = text.color.toColor()
    val displayedColor = if (textColor.luminance() < 0.5f) Color.White else Color.Black

    Text(
        text = text.description,
        color = when (text.style) {
            TextStyle.NORMAL -> textColor
            TextStyle.WITH_BACKGROUND -> displayedColor
            TextStyle.WITHOUT_BACKGROUND -> Color.White
        },
        fontFamily = text.font.toFontFamily(),
        textAlign = text.align.toTextAlign(),
        modifier = Modifier
            .offset { IntOffset(text.offsetX.roundToInt(), text.offsetY.roundToInt()) }
            .background(
                color = when (text.style) {
                    TextStyle.NORMAL -> Color.Transparent
                    TextStyle.WITH_BACKGROUND -> textColor
                    TextStyle.WITHOUT_BACKGROUND -> textColor.copy(alpha = 0.3f)
                },
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    )
}