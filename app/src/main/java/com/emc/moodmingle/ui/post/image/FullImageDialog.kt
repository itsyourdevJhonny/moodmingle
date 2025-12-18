package com.emc.moodmingle.ui.post.image

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.post.action.ShareAction
import com.emc.moodmingle.utils.modifier.drawGradient
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.abs

@Composable
fun FullImageDialog(
    fullImageUrl: String,
    onDismiss: () -> Unit,
    onShowShareSheet: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(0.8f) }
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    var isZoomedIn by remember { mutableStateOf(false) }

    val fileName = fullImageUrl.substringAfterLast("/")

    Dialog(
        onDismissRequest = {
            scope.launch {
                scale.animateTo(0.8f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                alpha.animateTo(0f)
                onDismiss()
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        LaunchedEffect(Unit) {
            alpha.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale.value,
                    scaleY = scale.value,
                    translationY = offsetY.value,
                    alpha = alpha.value
                )
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = fullImageUrl,
                contentDescription = "Zoomable Image",
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val newScale = (scale.value * zoom).coerceIn(1f, 4f)
                            scope.launch { scale.snapTo(newScale) }

                            if (scale.value > 1f) {
                                scope.launch { offsetY.snapTo(offsetY.value + pan.y) }
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                scope.launch {
                                    val target = if (isZoomedIn) 1f else 2.5f
                                    scale.animateTo(
                                        target,
                                        spring(dampingRatio = Spring.DampingRatioLowBouncy)
                                    )
                                    if (!isZoomedIn) offsetY.snapTo(0f)
                                    isZoomedIn = !isZoomedIn
                                }
                            },
                            onTap = {
                                scope.launch {
                                    scale.animateTo(
                                        0.8f,
                                        spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                    )
                                    alpha.animateTo(0f)
                                    onDismiss()
                                }
                            }
                        )
                    }
                    .pointerInput(scale.value) {
                        if (scale.value == 1f) {
                            detectVerticalDragGestures(
                                onVerticalDrag = { _, dragAmount ->
                                    scope.launch { offsetY.snapTo(offsetY.value + dragAmount) }
                                },
                                onDragEnd = {
                                    scope.launch {
                                        if (abs(offsetY.value) > 200f) {
                                            scale.animateTo(
                                                0.8f,
                                                spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                            )
                                            alpha.animateTo(0f)
                                            onDismiss()
                                        } else {
                                            offsetY.animateTo(
                                                0f,
                                                spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    },
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.download_outlined),
                    contentDescription = "Download",
                    modifier = Modifier
                        .size(32.dp)
                        .graphicsLayer(alpha = 0.7f)
                        .drawGradient()
                        .clickable {
                            if (isImageInGallery(fileName)) {
                                Toast.makeText(context, "Image already saved", Toast.LENGTH_SHORT).show()
                                return@clickable
                            }

                            downloadImage(context, fullImageUrl, fileName)
                            Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT)
                                .show()
                        }
                )

                ShareAction(
                    onShowShareSheet = onShowShareSheet,
                    modifier = Modifier
                        .size(32.dp)
                        .graphicsLayer(alpha = 0.7f)
                        .drawGradient(),
                    iconRes = R.drawable.share_outlined
                )
            }
        }
    }
}

fun isImageInGallery(fileName: String): Boolean {
    val pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val file = File(pictures, fileName)
    return file.exists()
}

fun downloadImage(context: Context, url: String, fileName: String = "image.jpg") {
    val request = DownloadManager.Request(url.toUri())
        .setTitle(fileName)
        .setDescription("Downloading image…")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, fileName)
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)

    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    dm.enqueue(request)
}
