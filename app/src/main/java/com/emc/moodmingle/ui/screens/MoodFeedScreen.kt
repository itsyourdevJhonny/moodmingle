package com.emc.moodmingle.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.post.AvatarImage
import com.emc.moodmingle.ui.post.MultimediaCard
import com.emc.moodmingle.ui.post.data.Post
import com.emc.moodmingle.ui.post.PostActions
import com.emc.moodmingle.ui.post.PostAudio
import com.emc.moodmingle.ui.post.PostImage
import com.emc.moodmingle.ui.post.PostText
import com.emc.moodmingle.ui.post.PostVideo
import com.emc.moodmingle.ui.post.data.dummyPosts
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.PurplePrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("UseKtx")
@Composable
fun MoodFeedScreen(onCreateClick: () -> Unit) {
    val posts = dummyPosts()
    var displayedPosts by remember { mutableStateOf(posts.take(4)) }

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filter { it != null }
            .distinctUntilChanged()
            .collect { lastVisible ->
                val index = lastVisible ?: 0
                if (index >= displayedPosts.lastIndex - 1 && displayedPosts.size < posts.size) {
                    delay(300)
                    displayedPosts = posts.take(displayedPosts.size + 3)
                }
            }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        userScrollEnabled = true,
        state = listState
    ) {
        item { CreatePostSection(onCreateClick) }

        item { TrendingMoodsSection() }

        item { MoodFilterSection() }

        item {
            Text(
                text = "Happy Moods",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = Color.White,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )
        }

        itemsIndexed(
            items = displayedPosts,
            key = { index, post -> post.hashCode() }
        ) { index, post ->
            when (post.type) {
                "text" -> {
                    MultimediaCard(
                        composable = { PostText(post.content) },
                        post = post
                    )
                }

                "image" -> {
                    MultimediaCard(
                        composable = { PostImage(post.imageRes, post.imageRes) },
                        post = post
                    )
                }

                "video" -> {
                    MultimediaCard(
                        composable = { PostVideo(post.videoRes!!) },
                        post = post
                    )
                }

                "audio" -> {
                    MultimediaCard(
                        composable = { PostAudio(post.audioRes) },
                        post = post
                    )
                }
            }
        }

        item { LoadingMorePosts() }
    }
}

@Composable
fun CreatePostSection(onCreateClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .background(
                Brush.linearGradient(colors = listOf(PurplePrimary, PurpleDark)),
                shape = RoundedCornerShape(30.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        onClick = onCreateClick
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Gradient Add",
            tint = Color.White
        )
        Text(text = "What's on you mood?", fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun TrendingMoodsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(colors = listOf(PurplePrimary, PurpleDark)),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "🔥 Trending Moods",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MoodChip("Happy", "😄")
                MoodChip("Calm", "😌")
                MoodChip("Excited", "🤩")
            }
        }
    }
}

@Composable
fun MoodFilterSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(colors = listOf(PurplePrimary, PurpleDark)),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Filter",
                    tint = Color(0xFF4A00E0)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Filter by Mood", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "😄 Happy",
                    "😢 Sad",
                    "🤩 Excited",
                    "😌 Calm",
                    "😬 Anxious",
                    "😡 Angry",
                    "🙏 Grateful",
                    "😕 Confused"
                ).forEach { TextChip(it) }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Clear filter",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun MoodChip(text: String, emoji: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF0E6FF)
    ) {
        Text(
            text = "$emoji $text",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 14.sp,
            color = Color(0xFF4A00E0),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun TextChip(label: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF7F5FF)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            fontSize = 13.sp,
            color = Color(0xFF4A00E0)
        )
    }
}

/*@androidx.annotation.OptIn(UnstableApi::class)
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun VideoPlayer(uri: Uri, modifier: Modifier = Modifier, exoPlayer: ExoPlayer) {
    val context = LocalContext.current

    var aspectRatio by remember { mutableFloatStateOf(16f / 9f) }

    exoPlayer.apply {
        repeatMode = Player.REPEAT_MODE_ALL
        setMediaItem(MediaItem.fromUri(uri))
        prepare()
        playWhenReady = true
    }

    LaunchedEffect(exoPlayer) {
        exoPlayer.addListener(object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                val ratio = if (videoSize.height != 0) {
                    videoSize.width.toFloat() / videoSize.height.toFloat()
                } else {
                    16f / 9f
                }
                aspectRatio = ratio
            }
        })
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val parentWidth = constraints.maxWidth.toFloat()
        val videoHeight = parentWidth / aspectRatio

        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = true
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier
                .width(with(LocalDensity.current) { parentWidth.toDp() })
                .height(with(LocalDensity.current) { videoHeight.toDp() })
        )
    }

    exoPlayer.addListener(object : Player.Listener {
        @androidx.annotation.OptIn(UnstableApi::class)
        override fun onPlayerError(error: PlaybackException) {
            Log.e("VideoPlayer", "Playback error: ${error.message}")
            Toast.makeText(context, "Video format not supported", Toast.LENGTH_SHORT).show()
        }
    })
}*/

@Composable
fun MoodPostCard(post: Post) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = Color.Blue
            )
            .background(brush = BrushPrimaryGradient, shape = RoundedCornerShape(18.dp)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    AvatarImage(post.avatar)

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = post.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = post.timeAgo,
                            fontSize = 12.sp,
                            color = GrayTextColor
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.more),
                        contentDescription = "More",
                        tint = Color.White
                    )

                    Surface(
                        color = Color(0xFFF0E6FF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${post.moodEmoji} ${post.mood}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color(0xFF4A00E0)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = post.content,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                color = Color.White
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                thickness = 1.dp,
                color = Color.White
            )

            PostActions(post)
        }
    }
}

@Composable
fun LoadingMorePosts() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        text = "Loading more post...",
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        ),
        textAlign = TextAlign.Center,
        color = Color.Gray
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun PreviewMoodFeed() {
    MoodFeedScreen(onCreateClick = {})
}
