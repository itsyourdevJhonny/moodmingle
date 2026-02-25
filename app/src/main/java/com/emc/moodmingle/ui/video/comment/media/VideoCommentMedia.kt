package com.emc.moodmingle.ui.video.comment.media

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.video.VideoComment
import com.emc.moodmingle.ui.settings.saved.media.getMime
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.ui.video.comment.media.dialog.VideoCommentMediaDialog
import com.emc.moodmingle.utils.exoplayer.ExoPlayerUtils
import com.emc.moodmingle.utils.media.image.ImageUtils
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel

@Composable
fun VideoCommentMedia(comment: VideoComment) {
    val mediaUrls = comment.mediaUrls

    val userViewModel = hiltViewModel<FirebaseUserViewModel>()

    val commenterResult by remember(comment.commenterId) {
        userViewModel.getUserByUid(comment.commenterId)
    }.collectAsState(initial = null)

    val commenter = commenterResult?.getOrNull()

    var showMedia by remember { mutableStateOf(false) }

    if (mediaUrls.isNotEmpty()) {
        AnimatedVisibility(
            modifier = Modifier.padding(top = 8.dp),
            visible = mediaUrls.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showMedia = true },
            ) {
                when (mediaUrls.size) {
                    1 -> SingleMedia(mediaUrls[0])
                    2 -> DoubleMedia(mediaUrls)
                    3 -> TripleMedia(mediaUrls)
                    4 -> MultipleMedia(mediaUrls)
                }
            }
        }
    }

    AnimatedVisibility(
        visible = showMedia,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        )
    ) {
        VideoCommentMediaDialog(
            commenterUsername = commenter?.username ?: "",
            mediaUrls,
            onDismiss = { showMedia = false }
        )
    }
}

@Composable
fun SingleMedia(url: String) {
    Box(modifier = Modifier.size(300.dp)) {
        DisplayMedia(url)
    }
}

@Composable
fun DoubleMedia(urls: List<String>) {
    Row {
        urls.forEach { url ->
            Box(modifier = Modifier.size(height = 200.dp, width = 150.dp)) {
                DisplayMedia(url)
            }
        }
    }
}

@Composable
fun TripleMedia(urls: List<String>) {
    Row {
        Box(modifier = Modifier.size(200.dp)) {
            DisplayMedia(urls[0])
        }
        Column {
            Box(modifier = Modifier.size(100.dp)) {
                DisplayMedia(urls[1])
            }
            Box(modifier = Modifier.size(100.dp)) {
                DisplayMedia(urls[2])
            }
        }
    }
}

@Composable
fun MultipleMedia(urls: List<String>) {
    Row {
        Column {
            Box(modifier = Modifier.size(150.dp)) {
                DisplayMedia(urls[0])
            }
            Box(modifier = Modifier.size(150.dp)) {
                DisplayMedia(urls[1])
            }
        }
        Column {
            Box(modifier = Modifier.size(150.dp)) {
                DisplayMedia(urls[2])
            }
            Box(
                modifier = Modifier.size(150.dp),
                contentAlignment = Alignment.Center
            ) {
                DisplayMedia(urls[3])
                if (urls.size > 4) {
                    MoreMediaCount(count = urls.size - 4)
                }
            }
        }
    }
}

@Composable
fun MoreMediaCount(count: Int) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "+$count", style = Typography.bodyLarge.copy(color = Color.White))
    }
}

@Composable
fun DisplayMedia(url: String) {
    Box {
        when {
            getMime(url).startsWith("image") -> ImageMedia(url)
            getMime(url).startsWith("video") -> VideoMedia(url)
            else -> AudioMedia(url)
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .offset(x = 4.dp, y = 4.dp)
                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    when {
                        getMime(url).startsWith("image") -> R.drawable.image
                        getMime(url).startsWith("video") -> R.drawable.video
                        else -> R.drawable.audio
                    }
                ),
                contentDescription = "Image",
                modifier = Modifier
                    .size(20.dp)
                    .drawGradient()
            )
        }
    }
}

@Composable
fun ImageMedia(url: String) {
    Image(
        painter = rememberAsyncImagePainter(url),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun VideoMedia(url: String, frameMillis: Long = 1_000L) {
    val context = LocalContext.current

    val imageLoader = remember {
        ImageUtils.provideVideoImageLoader(context)
    }

    var durationText by remember { mutableStateOf("") }

    LaunchedEffect(url) {
        ExoPlayerUtils.getDurationTextFrom(
            url,
            context,
            durationText,
            onDuration = { durationText = it }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(url)
                .videoFrameMillis(frameMillis)
                .crossfade(true)
                .build(),
            imageLoader = imageLoader,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        DisplayDuration(durationText)
    }
}

@Composable
fun AudioMedia(url: String) {
    val context = LocalContext.current

    var durationText by remember { mutableStateOf("") }

    LaunchedEffect(url) {
        ExoPlayerUtils.getDurationTextFrom(
            url,
            context,
            durationText,
            onDuration = { durationText = it }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SecondaryDark),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(60.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(R.drawable.music_disk),
                contentDescription = null,
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )
        }
        DisplayDuration(durationText)
    }
}

@Composable
fun BoxScope.DisplayDuration(durationText: String) {
    if (durationText.isEmpty()) {
        Box(
            modifier = Modifier
                .height(24.dp)
                .align(Alignment.BottomEnd),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                strokeWidth = 1.dp,
                modifier = Modifier.size(12.dp),
                color = Color.White
            )
        }
    } else {
        Text(text = durationText, modifier = Modifier.align(Alignment.BottomEnd))
    }
}