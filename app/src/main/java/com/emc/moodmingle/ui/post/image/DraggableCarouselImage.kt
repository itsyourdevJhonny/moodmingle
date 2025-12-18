package com.emc.moodmingle.ui.post.image

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset

@Composable
fun DraggableCarouselImage(
    imageUrls: List<String>,
    selectedIndex: Int,
    onSwipeTo: (Int) -> Unit,
    onOpen: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }

    var width by remember { mutableFloatStateOf(0f) }

    // STORE CURRENT + NEXT URL
    val currentUrl = imageUrls[selectedIndex + 1.coerceIn(1, imageUrls.lastIndex)]
    val nextUrl = imageUrls.getOrNull(selectedIndex + 1)
    val prevUrl = imageUrls.getOrNull(selectedIndex - 1)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 360.dp)
            .onGloballyPositioned { width = it.size.width.toFloat() }
            .pointerInput(selectedIndex) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        /*when {
                            offsetX.value < -width / 4 && nextUrl != null -> {
                                onSwipeTo(selectedIndex + 1)
                            }
                            offsetX.value > width / 4 && prevUrl != null -> {
                                onSwipeTo(selectedIndex - 1)
                            }
                        }*/

                        when {
                            offsetX.value < -width / 4 && selectedIndex < imageUrls.lastIndex -> {
                                onSwipeTo(selectedIndex + 1)
                            }
                            offsetX.value > width / 4 && selectedIndex > 0 -> {
                                onSwipeTo(selectedIndex - 1)
                            }
                        }

                        scope.launch { offsetX.animateTo(0f, tween(250)) }
                    }
                ) { change, drag ->
                    change.consume()

                    // BLOCK SWIPE IF NO NEXT/PREV
                    val canSwipeLeft = nextUrl != null || drag > 0
                    val canSwipeRight = prevUrl != null || drag < 0

                    if (canSwipeLeft && canSwipeRight) {
                        scope.launch { offsetX.snapTo(offsetX.value + drag) }
                    }
                }
            }
    ) {

        // ------------------------------------------------
        // CURRENT IMAGE
        // ------------------------------------------------
        if (currentUrl != null) {
            SubcomposeAsyncImage(
                model = currentUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .clickable { onOpen() }
            )
        }

        // ------------------------------------------------
        // NEXT IMAGE (VISIBLE ON SWIPE LEFT)
        // ------------------------------------------------
        if (nextUrl != null) {
            SubcomposeAsyncImage(
                model = nextUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .offset {
                        IntOffset(
                            (width + offsetX.value).roundToInt(),
                            0
                        )
                    }
            )
        }

        // ------------------------------------------------
        // PREVIOUS IMAGE (VISIBLE ON SWIPE RIGHT)
        // ------------------------------------------------
        if (prevUrl != null) {
            SubcomposeAsyncImage(
                model = prevUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .offset {
                        IntOffset(
                            (-width + offsetX.value).roundToInt(),
                            0
                        )
                    }
            )
        }
    }
}
