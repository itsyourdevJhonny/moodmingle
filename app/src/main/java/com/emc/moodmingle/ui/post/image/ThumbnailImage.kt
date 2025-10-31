package com.emc.moodmingle.ui.post.image

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient

@Composable
fun ThumbnailImage(thumbnailRes: Int, onImageClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onImageClick() })
            }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(thumbnailRes)
                .size(200, 200)
                .build(),
            contentDescription = "Thumbnail",
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Icon(
            painter = painterResource(R.drawable.image),
            modifier = Modifier
                .align(Alignment.Center)
                .size(60.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush = BrushPrimaryGradient,
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                },
            contentDescription = "Image"
        )
    }
}