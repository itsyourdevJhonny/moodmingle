package com.emc.moodmingle.ui.post

import android.content.Context
import android.webkit.MimeTypeMap
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.ui.post.image.FullImageDialog
import com.emc.moodmingle.ui.theme.BrushGrayGradient
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.viewmodel.local.PostViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun PostMedia(mediaUrls: List<String>, onShowShareSheet: (Boolean) -> Unit) {
    val context = LocalContext.current
    val postViewModel = hiltViewModel<PostViewModel>()
    val scope = rememberCoroutineScope()
    val state = rememberLazyListState()
    var showFullImage by remember { mutableStateOf(false) }
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val selectedUrl = mediaUrls[selectedIndex]
    val offsetX = remember { Animatable(0f) }

    if (mediaUrls.isEmpty()) return

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (mediaUrls.size > 1) {
            TotalSizeIndicator(selectedIndex = selectedIndex, size = mediaUrls.size)
        }

        var boxWidth by remember { mutableIntStateOf(0) }

        Box(
            modifier = Modifier
                .heightIn(min = 360.dp, max = 360.dp)
                .fillMaxWidth()
                .onSizeChanged { size -> boxWidth = size.width }
                .pointerInput(selectedIndex) {
                    if (mediaUrls.size != 1) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                val threshold = boxWidth / 4f

                                when {
                                    offsetX.value <= -threshold && selectedIndex < mediaUrls.lastIndex -> {
                                        scope.launch {
                                            offsetX.animateTo(-boxWidth.toFloat(), tween(250))
                                            selectedIndex += 1
                                            offsetX.snapTo(0f)
                                        }
                                    }

                                    offsetX.value >= threshold && selectedIndex > 0 -> {
                                        scope.launch {
                                            offsetX.animateTo(boxWidth.toFloat(), tween(250))
                                            selectedIndex -= 1
                                            offsetX.snapTo(0f)
                                        }
                                    }

                                    else -> scope.launch {
                                        offsetX.animateTo(0f, tween(250))
                                    }
                                }
                            }
                        ) { change, dragAmount ->
                            change.consume()
                            scope.launch { offsetX.snapTo(offsetX.value + dragAmount) }
                        }
                    }
                }
        ) {
            if (boxWidth > 0) {
                if (selectedIndex > 0) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .offset { IntOffset((offsetX.value - boxWidth).roundToInt(), 0) }
                    ) {
                        RenderMedia(
                            url = mediaUrls[selectedIndex - 1],
                            context = context,
                            onShowFullImage = { showFullImage = it }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                ) {
                    RenderMedia(
                        url = mediaUrls[selectedIndex],
                        context = context,
                        onShowFullImage = { showFullImage = it }
                    )
                }

                if (selectedIndex < mediaUrls.lastIndex) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .offset { IntOffset((offsetX.value + boxWidth).roundToInt(), 0) }
                    ) {
                        RenderMedia(
                            url = mediaUrls[selectedIndex + 1],
                            context = context,
                            onShowFullImage = { showFullImage = it }
                        )
                    }
                }
            }
        }

        if (mediaUrls.size > 1) {
            PositionDotsIndicator(mediaUrls, selectedIndex)

            LazyRow(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                state = state
            ) {
                items(mediaUrls) { url ->
                    val idx = mediaUrls.indexOf(url)
                    val mediaType = detectMediaType(url)

                    val animatedScale by animateFloatAsState(
                        targetValue = if (idx == selectedIndex) 1.15f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = ""
                    )

                    val borderAlpha by animateFloatAsState(
                        targetValue = if (idx == selectedIndex) 1f else 0f,
                        animationSpec = tween(250),
                        label = ""
                    )

                    val transition = updateTransition(selectedIndex, label = "")

                    val slideX by transition.animateFloat(
                        transitionSpec = { tween(150) },
                        label = ""
                    ) { selected ->
                        when {
                            idx == selected -> 0f
                            idx < selected -> -20f
                            else -> 20f
                        }
                    }

                    val fadeAlpha by animateFloatAsState(
                        targetValue = if (idx == selectedIndex) 1f else 0.6f,
                        animationSpec = tween(150),
                        label = ""
                    )

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .graphicsLayer {
                                scaleX = animatedScale
                                scaleY = animatedScale
                                translationX = slideX
                                alpha = fadeAlpha
                            }
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = 2.dp,
                                color = Color.White.copy(alpha = borderAlpha),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                scope.launch {
                                    offsetX.animateTo(
                                        if (idx > selectedIndex) -300f else 300f,
                                        tween(200)
                                    )
                                    selectedIndex = idx
                                    offsetX.animateTo(0f, tween(250))
                                    state.animateScrollToItem(selectedIndex)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when (mediaType) {
                            "image" -> ImageThumbnail(url, context)
                            "video" -> VideoThumbnail(url, postViewModel)
                            "audio" -> AudioThumbnail()
                        }
                    }
                }
            }
        }
    }

    if (showFullImage) {
        FullImageDialog(
            fullImageUrl = selectedUrl,
            onDismiss = { showFullImage = false },
            onShowShareSheet = onShowShareSheet
        )
    }
}

@Composable
fun RenderMedia(url: String, context: Context, onShowFullImage: (Boolean) -> Unit) {
    when (detectMediaType(url)) {
        "image" -> PostImage(url, onShowFullImage, context)
        "video" -> PostVideo(url)
        "audio" -> PostAudio(url)
    }
}

@Composable
fun TotalSizeIndicator(selectedIndex: Int, size: Int) {
    Text(
        text = "${selectedIndex + 1}/$size",
        style = MaterialTheme.typography.titleSmall.copy(
            color = Color.White,
            textAlign = TextAlign.End
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 24.dp)
    )
}

@Composable
fun PositionDotsIndicator(mediaUrls: List<String>, selectedIndex: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        mediaUrls.forEachIndexed { index, _ ->
            Box(
                modifier = Modifier
                    .size(if (index == selectedIndex) 10.dp else 6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(brush = if (index == selectedIndex) BrushPrimaryGradient else BrushGrayGradient)
            )
        }
    }
}

fun detectMediaType(url: String): String {
    val uri = url.toUri()
    val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())

    val mimeType = MimeTypeMap
        .getSingleton()
        .getMimeTypeFromExtension(extension.lowercase())

    return when {
        mimeType == null -> "unknown"
        mimeType.startsWith("image") -> "image"
        mimeType.startsWith("video") -> "video"
        mimeType.startsWith("audio") -> "audio"
        else -> "unknown"
    }
}
