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
fun PostImage(
    /*imageUrls: List<String>,
    onShowShareSheet: (Boolean) -> Unit*/
    selectedUrl: String,
    onShowFullImage: (Boolean) -> Unit,
    context: Context
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
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

        Box(
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        ) {
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

    /*if (imageUrls.isEmpty()) return

    val scope = rememberCoroutineScope()
    val state = rememberLazyListState()
    var selectedIndex by remember { mutableIntStateOf(0) }
    var showFullImage by remember { mutableStateOf(false) }

    val offsetX = remember { Animatable(0f) }
    val context = LocalContext.current

    val selectedUrl = imageUrls[selectedIndex]

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "${selectedIndex + 1}/${imageUrls.size}",
            style = MaterialTheme.typography.titleSmall.copy(
                color = Color.White,
                textAlign = TextAlign.End
            ),
            modifier = Modifier.fillMaxWidth().padding(end = 24.dp, top = 8.dp)
        )

        // --- MAIN IMAGE WITH SLIDE + FADE ---
        Box(
            modifier = Modifier
                .heightIn(max = 360.dp)
                .fillMaxWidth()
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(selectedUrl)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .crossfade(true)
                    .build(),
                contentDescription = "image",
                loading = { PostSkeletonItem() },
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .graphicsLayer { alpha = 1f - (offsetX.value / 1000).coerceIn(0f, 0.5f) }
                    .pointerInput(selectedIndex) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                val width = size.width.toFloat()
                                val threshold = width / 4
                                when {
                                    offsetX.value <= -threshold && selectedIndex < imageUrls.lastIndex -> {
                                        scope.launch {
                                            offsetX.animateTo(-width, tween(250))
                                            selectedIndex += 1
                                            offsetX.snapTo(width)
                                            offsetX.animateTo(0f, tween(250))
                                            state.animateScrollToItem(selectedIndex)
                                        }
                                    }
                                    offsetX.value >= threshold && selectedIndex > 0 -> {
                                        scope.launch {
                                            offsetX.animateTo(width, tween(250))
                                            selectedIndex -= 1
                                            offsetX.snapTo(-width)
                                            offsetX.animateTo(0f, tween(250))
                                            state.animateScrollToItem(selectedIndex)
                                        }
                                    }
                                    else -> scope.launch { offsetX.animateTo(0f, tween(250)) }
                                }
                            }
                        ) { change, dragAmount ->
                            change.consume()
                            scope.launch { offsetX.snapTo(offsetX.value + dragAmount) }
                        }
                    }
                    .clickable { showFullImage = true }
            )
        }

        // --- POSITION DOTS ---
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            imageUrls.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .size(if (index == selectedIndex) 10.dp else 6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (index == selectedIndex) Color.White else Color.Gray.copy(alpha = 0.5f))
                )
            }
        }

        // --- THUMBNAILS WITH SCALE EFFECT & LAZY LOADING ---
        LazyRow(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            state = state
        ) {
            items(imageUrls) { url ->
                val idx = imageUrls.indexOf(url)
                val scale = if (idx == selectedIndex) 1.1f else 1f

                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(url)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = "",
                    modifier = Modifier
                        .size(54.dp)
                        .graphicsLayer { scaleX = scale; scaleY = scale }
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = if (idx == selectedIndex) 2.dp else 0.dp,
                            color = if (idx == selectedIndex) Color.White else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            if (idx != selectedIndex) {
                                scope.launch {
                                    offsetX.animateTo(if (idx > selectedIndex) -300f else 300f, tween(200))
                                    selectedIndex = idx
                                    offsetX.animateTo(0f, tween(250))
                                    state.animateScrollToItem(selectedIndex)
                                }
                            }
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }

    if (showFullImage) {
        FullImageDialog(
            fullImageUrl = selectedUrl,
            onDismiss = { showFullImage = false },
            onShowShareSheet = onShowShareSheet
        )
    }*/
}