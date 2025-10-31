package com.emc.moodmingle.ui.post.image

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun FullImageDialog(fullImageRes: Int, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = { onDismiss() },
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
                            onTap = { onDismiss() }
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
                                        onDismiss()
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