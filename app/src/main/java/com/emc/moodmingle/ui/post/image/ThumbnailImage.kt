package com.emc.moodmingle.ui.post.image

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.post.skeleton.PostSkeletonItem
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun ThumbnailImage(thumbnailRes: String, onImageClick: () -> Unit, onShowImage: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onImageClick() },
                    onLongPress = {
                        onShowImage(true)
                    },
                )
            }
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(thumbnailRes)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .build(),
            contentDescription = "Thumbnail",
            loading = { PostSkeletonItem() },
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.image),
                        contentDescription = "Image load error",
                        tint = Color.White.copy(alpha = 0.6f)
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .size(220.dp),
            contentScale = ContentScale.Crop
        )

        Icon(
            painter = painterResource(R.drawable.image),
            modifier = Modifier
                .align(Alignment.Center)
                .size(32.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawGradient(),
            contentDescription = "Image"
        )
    }
}
