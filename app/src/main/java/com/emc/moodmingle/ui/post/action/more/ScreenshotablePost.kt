package com.emc.moodmingle.ui.post.action.more

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.createBitmap
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ScreenshotablePost(post: @Composable () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var screenshotPreview by remember { mutableStateOf(false) }
    var hideButtons by remember { mutableStateOf(false) }
    var triggerCapture: (() -> Unit)? by remember { mutableStateOf(null) }

    // Flash animation state
    var flash by remember { mutableStateOf(false) }
    val flashAlpha by animateFloatAsState(
        targetValue = if (flash) 1f else 0f,
        animationSpec = tween(250)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = {
                screenshotPreview = true
                hideButtons = true
                triggerCapture?.invoke()
                flash = true
                scope.launch {
                    delay(300)
                    flash = false
                    delay(300)
                    hideButtons = false
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .background(BrushPrimaryGradient, CircleShape)
        ) {
            Icon(
                painter = painterResource(R.drawable.screenshot),
                contentDescription = "Screenshot",
                tint = Color.White
            )
        }
    }

    if (flash) {
        AndroidView(
            factory = { context ->
                ComposeView(context).apply {
                    setContent {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .onGloballyPositioned {
                                    val bitmap = captureComposable(this)
                                    capturedBitmap = bitmap
                                }
                        ) {
                            post()
                        }
                        /*post {
                            val bitmap = captureComposable(this)
                            capturedBitmap = bitmap
                            screenshotPreview = true
                        }*/
                    }
                }
            }
        )
    } else {
        ComposeView(context).apply {
            setContent {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .onGloballyPositioned {
                            val bitmap = captureComposable(this)
                            capturedBitmap = bitmap
                        }
                ) {
                    post()
                }
                /*post {
                    val bitmap = captureComposable(this)
                    capturedBitmap = bitmap
                    screenshotPreview = true
                }*/
            }
        }
    }

    /*Box(modifier = Modifier.fillMaxWidth()) {

        ScreenshotWrapper(
            content = { post() },
            onBitmapCaptured = { bitmap ->
                capturedBitmap = bitmap
                screenshotPreview = true
                flash = true
            },
            onCaptureReady = { lambda ->
                triggerCapture = lambda
            },
            hideUI = hideButtons
        )

        // FLASH OVERLAY
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = flashAlpha))
        )

        // SCREENSHOT BUTTON
        if (!hideButtons && !screenshotPreview) {
            IconButton(
                onClick = {
                    hideButtons = true
                    triggerCapture?.invoke()
                    flash = true
                    scope.launch {
                        delay(300)
                        flash = false
                        delay(300)
                        hideButtons = false
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .background(BrushPrimaryGradient, CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.screenshot),
                    contentDescription = "Screenshot",
                    tint = Color.White
                )
            }
        }
    }*/

    // SCREENSHOT PREVIEW + OPTIONS
    if (screenshotPreview && capturedBitmap != null) {
        ScreenshotPreviewDialog(
            bitmap = capturedBitmap!!,
            onDismiss = {
                screenshotPreview = false
            },
            onSave = { saveToGallery(context, capturedBitmap!!) },
            onShare = { shareImage(context, capturedBitmap!!) }
        )
    }
}

@Composable
fun ScreenshotWrapper(
    content: @Composable () -> Unit,
    onBitmapCaptured: (Bitmap) -> Unit,
    onCaptureReady: (() -> Unit) -> Unit,
    hideUI: Boolean
) {
    val context = LocalContext.current
    var targetView: View? by remember { mutableStateOf(null) }

    AndroidView(
        factory = {
            ComposeView(context).apply {
                setContent {
                    /*Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(if (hideUI) 0f else 1f)
                            .onGloballyPositioned {
                                targetView = this
                            }
                    ) {*/
                        content()
//                    }
                }
            }
        },
        update = { targetView = it }
    )

    // inside ScreenshotWrapper composable (replace previous capture)
    val coroutineScope = rememberCoroutineScope()

    val capture: () -> Unit = {
        coroutineScope.launch {
            try {
                val v = targetView ?: return@launch
                // safe capture (handles PixelCopy + fallback)
                val bmp = safeCaptureViewBitmap(v)
                onBitmapCaptured(bmp)
            } catch (e: Exception) {
                // optional: log or show toast
                e.printStackTrace()
            }
        }
    }
    LaunchedEffect(Unit) {
        onCaptureReady(capture)
    }


    LaunchedEffect(Unit) {
        onCaptureReady(capture)
    }
}

fun captureComposable(view: View): Bitmap {
    val width = view.width.takeIf { it > 0 } ?: throw IllegalStateException("view width is zero")
    val height = view.height.takeIf { it > 0 } ?: throw IllegalStateException("view height is zero")
    val bitmap = createBitmap(width + 1, height + 1, Bitmap.Config.ARGB_8888)

    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}

@Composable
fun ScreenshotPreviewDialog(
    bitmap: Bitmap,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onShare: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.95f))
        ) {

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(bitmap.width.toFloat() / bitmap.height.toFloat()) // preserves aspect
                    .align(Alignment.Center)
                    .padding(12.dp),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }

                IconButton(onClick = onSave) {
                    Icon(
                        painter = painterResource(R.drawable.save_post),
                        contentDescription = "Save",
                        tint = Color.White
                    )
                }

                IconButton(onClick = onShare) {
                    Icon(
                        painter = painterResource(R.drawable.share),
                        contentDescription = "Share",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
