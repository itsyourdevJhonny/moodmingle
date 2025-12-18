package com.emc.moodmingle.ui.screens

import androidx.annotation.OptIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.data.firebase.model.PostEntityFirebase
import com.emc.moodmingle.ui.settings.saved.media.isVideo
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.ui.video.VideoFeedItem
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.firebase.PostViewModelFirebase
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoFeedScreen(onBack: () -> Unit) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val postViewModel = hiltViewModel<PostViewModelFirebase>()

    val currentUser by userViewModel.loggedUser
    val userId = currentUser?.uid ?: ""

    var posts by remember { mutableStateOf(emptyList<PostEntityFirebase>()) }

    LaunchedEffect(userId) {
        posts = postViewModel.getPostsByUserId(userId).filter { it.urls.isNotEmpty() }
    }

    val videoUrls = posts.flatMap { it.urls }.filter { isVideo(it) }

    var pageSize by rememberSaveable { mutableIntStateOf(10) }
    var isLoadingMore by rememberSaveable { mutableStateOf(false) }

    val pagedVideoUrls by remember(videoUrls, pageSize) {
        mutableStateOf(videoUrls.take(pageSize))
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { pagedVideoUrls.size }
    )

    LaunchedEffect(pagerState.currentPage) {
        val lastPage = pagedVideoUrls.lastIndex
        if (pagerState.currentPage >= lastPage && !isLoadingMore && pageSize < videoUrls.size) {
            isLoadingMore = true
            delay(300)
            pageSize += 10
            isLoadingMore = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, bottom = 42.dp)
            .background(PrimaryDark)
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            pageSize = PageSize.Fill,
            beyondViewportPageCount = 1,
            pageSpacing = 0.dp,
            snapPosition = SnapPosition.Start,
        ) { page ->
            VideoFeedItem(
                videoUrl = pagedVideoUrls[page],
                post = posts[page],
                isPageActive = pagerState.currentPage == page,
                modifier = Modifier.fillMaxSize()
            )
        }

        Header(
            onBack = onBack,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        if (isLoadingMore) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            )
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
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.clickable { onBack() },
            tint = Color.White
        )

        Text(
            text = "Videos",
            style = Typography.bodyLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.W900
            )
        )
    }
}