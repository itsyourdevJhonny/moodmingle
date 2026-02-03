package com.emc.moodmingle.utils.media

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.ui.post.AudioThumbnail
import com.emc.moodmingle.ui.post.ImageThumbnail
import com.emc.moodmingle.ui.post.VideoThumbnail
import com.emc.moodmingle.ui.post.detectMediaType
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.viewmodel.local.PostViewModel
import kotlinx.coroutines.launch

@Composable
fun MediaThumbnails(
    urls: List<String>,
    containerShape: Shape = RectangleShape,
    containerHeight: Dp = 365.dp
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val postViewModel = hiltViewModel<PostViewModel>()

    var pageSize by rememberSaveable { mutableIntStateOf(10) }
    val visibleUrls by remember(urls, pageSize) {
        derivedStateOf { urls.take(pageSize) }
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { visibleUrls.size }
    )

    // pagination: load more when near end
    LaunchedEffect(pagerState.currentPage, visibleUrls.size) {
        if (pagerState.currentPage >= visibleUrls.size - 3 && pageSize < urls.size) pageSize += 10
    }

    // auto-correct page if items removed
    LaunchedEffect(visibleUrls.size) {
        if (pagerState.currentPage >= visibleUrls.size && visibleUrls.isNotEmpty()) {
            pagerState.scrollToPage(visibleUrls.lastIndex)
        }
    }

    Box(modifier = Modifier.heightIn(max = containerHeight)) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(),
            modifier = Modifier
                .clip(containerShape)
                .background(PrimaryDark),
            beyondViewportPageCount = 1,
            key = { visibleUrls[it] }
        ) { page ->
            val url = visibleUrls[page]
            val mediaType = detectMediaType(url)

            when (mediaType) {
                "image" -> ImageThumbnail(url, context)
                "video" -> VideoThumbnail(url, postViewModel)
                "audio" -> AudioThumbnail()
            }
        }

        if (!pagerState.isScrollInProgress) {
            if (pagerState.currentPage == 1) {
                ScrollerButton(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp),
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } }
                )
            }

            if (pagerState.currentPage != urls.size - 1) {
                ScrollerButton(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp),
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } }
                )
            }
        }
    }
}

@Composable
private fun ScrollerButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clickable { onClick() }
            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            .padding(4.dp)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "Icon",
            tint = Color.White,
            modifier = Modifier.size(38.dp)
        )
    }
}