package com.emc.moodmingle.ui.post

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostImage(thumbnailRes: Int, fullImageRes: Int) {
    var showFullImage by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { showFullImage = true })
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

    if (showFullImage) {
        Dialog(
            onDismissRequest = { showFullImage = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            val scope = rememberCoroutineScope()
            val scale = remember { Animatable(1f) }
            val offsetX = remember { Animatable(0f) }
            val offsetY = remember { Animatable(0f) }
            var isZoomedIn by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = fullImageRes,
                    contentDescription = "Zoomable Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { centroid, pan, zoom, rotation ->
                                val newScale = (scale.value * zoom).coerceIn(1f, 4f)
                                scope.launch { scale.snapTo(newScale) }

                                if (scale.value > 1f) {
                                    val maxX = 800f * (scale.value - 1f)
                                    val maxY = 800f * (scale.value - 1f)
                                    val newX = (offsetX.value + pan.x).coerceIn(-maxX, maxX)
                                    val newY = (offsetY.value + pan.y).coerceIn(-maxY, maxY)

                                    scope.launch {
                                        offsetX.snapTo(newX)
                                        offsetY.snapTo(newY)
                                    }
                                }
                            }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    scope.launch {
                                        if (isZoomedIn) {
                                            scale.animateTo(
                                                targetValue = 1f,
                                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                            )
                                            offsetX.animateTo(0f)
                                            offsetY.animateTo(0f)
                                        } else {
                                            scale.animateTo(
                                                targetValue = 2.5f,
                                                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                                            )
                                        }
                                        isZoomedIn = !isZoomedIn
                                    }
                                },
                                onTap = { showFullImage = false }
                            )
                        }
                        .pointerInput(scale.value) {
                            if (scale.value == 1f) {
                                detectVerticalDragGestures(
                                    onVerticalDrag = { _, dragAmount ->
                                        scope.launch {
                                            offsetY.snapTo(offsetY.value + dragAmount)
                                        }
                                    },
                                    onDragEnd = {
                                        if (abs(offsetY.value) > 200f) {
                                            showFullImage = false
                                        } else {
                                            scope.launch { offsetY.animateTo(0f) }
                                        }
                                    }
                                )
                            }
                        }
                        .graphicsLayer(
                            scaleX = scale.value,
                            scaleY = scale.value,
                            translationX = offsetX.value,
                            translationY = offsetY.value
                        ),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

/*
package com.emc.moodmingle.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun PostImage(thumbnailRes: Int, fullImageRes: Int) {
    var showFullImage by remember { mutableStateOf(false) }

    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(1f) }
    var offsetY by remember { mutableFloatStateOf(1f) }
    var isZoomedIn by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { showFullImage = true }
            .shadow(elevation = 44.dp, ambientColor = Color.White, spotColor = Color.White)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(thumbnailRes)
                .size(300, 300)
                .build(),
            contentDescription = "Thumbnail Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
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

    if (showFullImage) {
        Dialog(
            onDismissRequest = { showFullImage = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = fullImageRes,
                    contentDescription = "Full Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            coroutineScope {
                                launch {
                                    detectTransformGestures(
                                        onGesture = { centroid, pan, zoom, rotation ->
                                            scale = (scale * zoom).coerceIn(1f, 5f)
                                            offsetX += pan.x
                                            offsetY += pan.y

                                            println("Scale : $scale")
                                            println("X     : $offsetX")
                                            println("Y     : $offsetY")
                                        }
                                    )
                                }
                                launch {
                                    detectTapGestures(
                                        onDoubleTap = {
                                            if (isZoomedIn) {
                                                scale = 1f
                                                offsetX = 0f
                                                offsetY = 0f
                                            } else {
                                                scale = 2f
                                            }
                                            isZoomedIn = !isZoomedIn
                                        },
                                        onTap = {
                                            showFullImage = false
                                        }
                                    )
                                }
                            }
                        }
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        ),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}*/
