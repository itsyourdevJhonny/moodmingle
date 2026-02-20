package com.emc.moodmingle.ui.settings.saved.media

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.emc.moodmingle.domain.remote.model.post.normal.PostEntityFirebase
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.domain.remote.model.saved.SaveEntityFirebase
import com.emc.moodmingle.ui.post.getVideoThumbnail
import com.emc.moodmingle.ui.post.skeleton.ShimmerAnimation
import com.emc.moodmingle.viewmodel.local.PostViewModel

@Composable
fun VideoGrid(
    save: SaveEntityFirebase,
    post: PostEntityFirebase,
    user: UserEntityFirebase,
    onShowSheet: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val postViewModel = hiltViewModel<PostViewModel>()

    PostInformation(save, post, user, onShowSheet = onShowSheet)

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        items(post.urls) { url ->
            if (isVideo(url)) {
                val cachedThumbnail = postViewModel.post.getCachedThumbnail(url)
                var thumbnail by remember(url) { mutableStateOf(cachedThumbnail) }

                var durationText by rememberSaveable { mutableStateOf("") }

                LaunchedEffect(url) {
                    extractDuration(
                        context,
                        url,
                        durationText,
                        onDurationText = { durationText = it }
                    )
                }

                LaunchedEffect(url) {
                    if (thumbnail == null) {
                        val generated = getVideoThumbnail(url)
                        if (generated != null) {
                            postViewModel.post.cacheThumbnail(url, generated)
                            thumbnail = generated
                        }
                    }
                }

                if (thumbnail != null) {
                    Box(contentAlignment = Alignment.Center) {
                        CacheImage(thumbnail)
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center,
                            content = { DurationText(durationText) }
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(ShimmerAnimation(), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {}
                }
            }
        }
    }
}

@Composable
private fun CacheImage(thumbnail: Bitmap?) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(thumbnail)
            .diskCachePolicy(CachePolicy.ENABLED)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build(),
        contentDescription = null,
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}
@Composable
private fun DurationText(durationText: String) {
    if (durationText.isEmpty()) {
        CircularProgressIndicator(
            strokeWidth = 1.dp,
            modifier = Modifier.size(12.dp),
            color = Color.White
        )
    } else {
        Text(
            modifier = Modifier
                .background(
                    Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 4.dp),
            text = durationText,
            color = Color.White,
            fontSize = 8.sp,
        )
    }
}

fun isVideo(url: String): Boolean {
    return url.endsWith("mp4")
            || url.endsWith("mov")
            || url.endsWith("mkv")
            || url.endsWith("webm")
            || url.endsWith("avi")
            || url.endsWith("flv")
}