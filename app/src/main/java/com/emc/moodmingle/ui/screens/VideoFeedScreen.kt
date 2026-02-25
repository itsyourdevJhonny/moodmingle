package com.emc.moodmingle.ui.screens

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.domain.remote.model.post.normal.PostEntityFirebase
import com.emc.moodmingle.domain.remote.model.video.VideoComment
import com.emc.moodmingle.ui.settings.saved.media.isVideo
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.ui.video.VideoFeedItem
import com.emc.moodmingle.ui.video.comment.bottomsheet.VideoCommentBottomSheet
import com.emc.moodmingle.ui.video.comment.reply.dialog.DisplayVideoCommentReplies
import com.emc.moodmingle.utils.exoplayer.ExoPlayerPool
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.remote.PostViewModelFirebase

data class VideoPageItem(
    val post: PostEntityFirebase,
    val videoUrl: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoFeedScreen(
    onUserClick: (String) -> Unit,
    onChatClick: (String, String) -> Unit,
    onRemix: (String, String) -> Unit,
    onBack: () -> Unit
) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val postViewModel = hiltViewModel<PostViewModelFirebase>()
    val context = LocalContext.current

    val currentUser by userViewModel.getLoggedUser().collectAsState(initial = null)
    var posts by remember { mutableStateOf(emptyList<PostEntityFirebase>()) }

    LaunchedEffect(Unit) {
        postViewModel.getAllPosts().collect { posts = it.sortedByDescending { p -> p.timeAgo } }
    }

    val allVideoItems by remember(posts, currentUser) {
        derivedStateOf {
            posts.flatMap { post ->
                post.urls
                    .filter { isVideo(it) && currentUser?.hiddenVideoUrls?.contains(it) == false }
                    .map { url -> VideoPageItem(post, url) }
            }
        }
    }

    var pageSize by rememberSaveable { mutableIntStateOf(10) }
    val visibleItems by remember(allVideoItems, pageSize) {
        derivedStateOf { allVideoItems.take(pageSize) }
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { visibleItems.size }
    )

    // pagination: load more when near end
    LaunchedEffect(pagerState.currentPage, visibleItems.size) {
        if (pagerState.currentPage >= visibleItems.size - 3 && pageSize < allVideoItems.size) pageSize += 10
    }

    // auto-correct page if items removed
    LaunchedEffect(visibleItems.size) {
        if (pagerState.currentPage >= visibleItems.size && visibleItems.isNotEmpty()) {
            pagerState.scrollToPage(visibleItems.lastIndex)
        }
    }

    val playerPool = remember { ExoPlayerPool(context) }

    var showCommentBottomSheet by rememberSaveable { mutableStateOf(false) }
    var selectedVideoUrl by rememberSaveable { mutableStateOf("") }
    var activeRepliesComment by remember { mutableStateOf<VideoComment?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, bottom = 42.dp)
            .background(PrimaryDark)
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1,
            key = { visibleItems[it].videoUrl }
        ) { page ->
            val item = visibleItems[page]

            val player = remember(item.videoUrl) { playerPool.acquire(item.videoUrl) }

            // preload next 2 videos
            LaunchedEffect(page) {
                visibleItems.getOrNull(page + 1)?.let { playerPool.preload(it.videoUrl) }
                visibleItems.getOrNull(page + 2)?.let { playerPool.preload(it.videoUrl) }
            }

            DisposableEffect(Unit) { onDispose { playerPool.release(player) } }

            VideoFeedItem(
                exoPlayer = player,
                videoUrl = item.videoUrl,
                post = item.post,
                isPageActive = pagerState.currentPage == page,
                modifier = Modifier.fillMaxSize(),
                onSelectedVideoUrl = { selectedVideoUrl = it },
                onShowCommentBottomSheet = { showCommentBottomSheet = it }
            )
        }

        Header(onBack = onBack, modifier = Modifier.align(Alignment.TopCenter))

        AnimatedVisibility(
            visible = showCommentBottomSheet,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
        ) {
            VideoCommentBottomSheet(
                onDismiss = { showCommentBottomSheet = false },
                selectedVideoUrl,
                onActiveRepliesComment = { activeRepliesComment = it },
                onUserClick,
                onChatClick,
                onRemix
            )
        }

        AnimatedVisibility(
            visible = activeRepliesComment != null,
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ),
            exit = slideOutHorizontally(
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
        ) {
            activeRepliesComment?.let { comment ->
                DisplayVideoCommentReplies(
                    comment,
                    userViewModel,
                    onDismiss = { activeRepliesComment = null }
                )
            }
        }
    }
}

@Composable
private fun Header(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color.Black.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBack() },
                tint = Color.White
            )
        }

        Box(
            modifier = Modifier.background(Color.Black.copy(alpha = 0.7f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Videos",
                style = Typography.bodyLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.W900
                ),
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
            )
        }
    }
}