package com.emc.moodmingle.ui.post

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.post.skeleton.ShimmerAnimation
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.viewmodel.firebase.PostViewModelFirebase
import com.emc.moodmingle.viewmodel.local.PostViewModel

@Composable
fun ImageThumbnail(url: String, context: Context) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build(),
        contentDescription = "",
        loading = { it.copy(painter = painterResource(R.drawable.image)) },
        contentScale = ContentScale.Crop
    )
}

@Composable
fun VideoThumbnail(
    url: String,
    postViewModel: PostViewModel
) {
    val cachedThumbnail = postViewModel.post.getCachedThumbnail(url)
//    var thumbnail by remember { mutableStateOf(cachedThumbnail) }
    var thumbnail by remember(url) { mutableStateOf(cachedThumbnail) }

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
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(thumbnail)
                .diskCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = "Video thumbnail",
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ShimmerAnimation()),
            contentAlignment = Alignment.Center
        ) {}
    }
}

@Composable
fun AudioThumbnail() {
    Box(
        modifier = Modifier
            .background(BrushPrimaryGradient, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp)
            .size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.music_note),
            contentDescription = "Music",
        )
    }
}