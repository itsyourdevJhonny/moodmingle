package com.emc.moodmingle.ui.post

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.post.skeleton.PostSkeletonItem
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun PostImage(selectedUrl: String, onShowFullImage: (Boolean) -> Unit, context: Context) {
    Box(modifier = Modifier.fillMaxSize()) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(selectedUrl)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .build(),
            loading = { PostSkeletonItem() },
            contentDescription = "image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 360.dp)
                .clickable { onShowFullImage(true) }
        )

        Box(modifier = Modifier.padding(start = 8.dp, top = 4.dp)) {
            Icon(
                painter = painterResource(R.drawable.image),
                contentDescription = "Image",
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.TopStart)
                    .graphicsLayer(alpha = 0.8f)
                    .drawGradient()
            )
        }
    }
}