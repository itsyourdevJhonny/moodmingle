package com.emc.moodmingle.ui.create

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.post.audio.AudioItemMini
import com.emc.moodmingle.ui.profile.DrawUserNoPaddingLine
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.utils.components.BackIcon
import com.emc.moodmingle.utils.modifier.drawGradient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AllMediaGallery(
    mediaUris: List<Uri>,
    onSelectedType: (String) -> Unit,
    onDismiss: (Boolean) -> Unit,
    onUploadedUri: (List<Uri>) -> Unit
) {
    val context = LocalContext.current
    var mediaUris by remember { mutableStateOf(mediaUris) }
    val mediaTypes = listOf("Image", "Video", "Audio")
    var type by remember { mutableStateOf("Image") }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Spacer(Modifier.height(38.dp))

            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                BackIcon(onClick = { onUploadedUri(emptyList()); onDismiss(false) })
                SelectMediaTitle()
                Tabs(mediaTypes, type, onSelectedType = { type = it })

                if (mediaUris.isEmpty()) {
                    Spacer(Modifier.height(8.dp))
                } else {
                    val mediaPlayers = remember { mutableStateMapOf<Uri, MediaPlayer>() }
                    var currentlyPlaying by remember { mutableStateOf<Uri?>(null) }

                    DisposableEffect(Unit) { onDispose { mediaPlayers.values.forEach { it.release() } } }

                    DrawUserNoPaddingLine(
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Selected(context, mediaUris)

                    val itemVisibility = remember { mutableStateMapOf<Uri, Boolean>() }

                    LazyRow(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        mediaUris.forEach { uri ->
                            item(uri.toString()) { // key by uri for stability
                                val visible = itemVisibility.getOrPut(uri) { false }

                                // trigger fadeIn on first composition
                                LaunchedEffect(uri) {
                                    if (!visible) {
                                        itemVisibility[uri] = true
                                    }
                                }

                                AnimatedVisibility(
                                    visible = itemVisibility[uri] == true,
                                    enter = fadeIn(animationSpec = tween(1000)) + scaleIn(),
                                    exit = fadeOut(animationSpec = tween(1000)) + scaleOut()
                                ) {
                                    Box(modifier = Modifier.size(80.dp)) {
                                        val mimeType = getMimeType(context, uri) ?: ""

                                        when {
                                            mimeType.startsWith("image") -> ImageThumbnail(uri)
                                            mimeType.startsWith("video") -> VideoThumbnail(videoUri = uri)
                                            mimeType.startsWith("audio") -> {
                                                val isPlaying = currentlyPlaying == uri

                                                AudioItemMini(
                                                    uri = uri,
                                                    isPlaying = isPlaying,
                                                    onClickPlay = {
                                                        currentlyPlaying?.let { prev ->
                                                            mediaPlayers[prev]?.pause()
                                                        }
                                                        val mp = mediaPlayers.getOrPut(uri) {
                                                            MediaPlayer.create(context, uri)
                                                        }
                                                        mp.start()
                                                        currentlyPlaying = uri
                                                    },
                                                    onClickPause = {
                                                        mediaPlayers[uri]?.pause()
                                                        currentlyPlaying = null
                                                    }
                                                )
                                            }
                                        }

                                        RemoveSingleIcon(
                                            itemVisibility,
                                            uri,
                                            mediaUris,
                                            onUrisSelected = { mediaUris = it }
                                        )
                                    }
                                }
                            }
                        }

                        if (mediaUris.isNotEmpty() && mediaUris.size > 1) {
                            item("remove_all") {
                                RemoveAllIcon(
                                    mediaUris,
                                    itemVisibility,
                                    onUrisSelected = { mediaUris = it }
                                )
                            }
                        }
                    }

                    UploadButton(type, mediaUris, onSelectedType, onUploadedUri, onDismiss)
                }
            }

            if (type.isNotBlank()) {
                DisplayGallery(type, mediaUris, onUrisSelected = { mediaUris = it })
            }
        }
    }
}

@Composable
fun ImageThumbnail(uri: Uri) {
    Image(
        painter = rememberAsyncImagePainter(uri),
        contentDescription = "Selected image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                brush = BrushPrimaryGradient,
                shape = RoundedCornerShape(8.dp)
            )
    )
}

@Composable
private fun SelectMediaTitle() {
    Text(
        text = "Select Image/Video/Audio",
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            fontStyle = FontStyle.Italic,
            color = GrayTextColor
        )
    )
}

@Composable
private fun Tabs(mediaTypes: List<String>, type: String, onSelectedType: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        mediaTypes.forEach { mediaType ->
            val isActive = mediaType == type

            // animate scale
            val scale by animateFloatAsState(
                targetValue = if (isActive) 1.15f else 1f,
                animationSpec = tween(250)
            )

            // animate alpha (fade effect)
            val alpha by animateFloatAsState(
                targetValue = if (isActive) 1f else 0.6f,
                animationSpec = tween(250)
            )

            Box(
                modifier = Modifier
                    .graphicsLayer(scaleX = scale, scaleY = scale, alpha = alpha)
                    .clip(RoundedCornerShape(8.dp))
                    .background(brush = if (isActive) BrushPrimaryGradient else SolidColor(Color.Transparent))
                    .size(59.dp)
                    .clickable { onSelectedType(mediaType) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        when (mediaType) {
                            "Image" -> R.drawable.image
                            "Video" -> R.drawable.video
                            "Audio" -> R.drawable.audio
                            else -> R.drawable.image
                        }
                    ),
                    contentDescription = mediaType,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun BoxScope.RemoveSingleIcon(
    itemVisibility: SnapshotStateMap<Uri, Boolean>,
    uri: Uri,
    uris: List<Uri>,
    onUrisSelected: (List<Uri>) -> Unit
) {
    IconButton(
        onClick = {
            // trigger exit animation first
            itemVisibility[uri] = false
            CoroutineScope(Dispatchers.IO).launch {
                delay(300)
                onUrisSelected(uris - uri)
                itemVisibility.remove(uri)
            }
        },
        modifier = Modifier
            .align(Alignment.TopEnd)
            .size(20.dp)
    ) {
        Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = Color.Red)
    }
}

@Composable
fun RemoveAllIcon(
    uris: List<Uri>,
    itemVisibility: SnapshotStateMap<Uri, Boolean>,
    onUrisSelected: (List<Uri>) -> Unit
) {
    IconButton(
        onClick = {
            // fade out all items
            uris.forEach { uri -> itemVisibility[uri] = false }

            // remove items after animation
            CoroutineScope(Dispatchers.IO).launch {
                delay(300)
                onUrisSelected(emptyList())
                itemVisibility.clear()
            }
        },
        modifier = Modifier.size(24.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Remove all",
            tint = Color.Red
        )
    }
}

@Composable
private fun ColumnScope.UploadButton(
    type: String,
    mediaUris: List<Uri>,
    onSelectedType: (String) -> Unit,
    onUploadedUri: (List<Uri>) -> Unit,
    onShowDialog: (Boolean) -> Unit
) {
    Button(
        onClick = {
            if (mediaUris.isNotEmpty()) {
                onSelectedType(type)
                onUploadedUri(mediaUris)
            }

            onShowDialog(false)
        },
        modifier = Modifier
            .padding(vertical = 8.dp)
            .align(Alignment.CenterHorizontally)
            .background(brush = BrushPrimaryGradient, shape = RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Icon(
            painter = painterResource(R.drawable.upload_image),
            contentDescription = "Upload",
            modifier = Modifier.size(24.dp),
            tint = Color.White
        )
        Text(
            text = "Upload (${mediaUris.size})",
            color = Color.White,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun DisplayGallery(
    type: String,
    mediaUris: List<Uri>,
    onUrisSelected: (List<Uri>) -> Unit
) {
    when (type) {
        "Image" -> ImageGallery(uris = mediaUris, onUrisSelected = onUrisSelected)
        "Video" -> VideoGallery(uris = mediaUris, onUrisSelected = onUrisSelected)
        "Audio" -> AudioGallery(selectedAudios = mediaUris, onSelectedAudio = onUrisSelected)
    }
}

@Composable
private fun Selected(context: Context, mediaUris: List<Uri> = emptyList()) {
    val (images, videos, audios) = countMediaTypes(context, mediaUris)

    Column(
        modifier = Modifier.padding(bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                modifier = Modifier
                    .size(18.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )
            Text(
                text = "Selected (${mediaUris.size})",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Count(images, "Image")
            Count(videos, "Video")
            Count(audios, "Audio")
        }
    }
}

@Composable
private fun Count(count: Int, type: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            painter = painterResource(
                id = when (type) {
                    "Image" -> R.drawable.image
                    "Video" -> R.drawable.video
                    "Audio" -> R.drawable.audio
                    else -> R.drawable.image
                }
            ),
            contentDescription = type,
            modifier = Modifier.size(14.dp)
        )

        Text(
            text = "$type ($count)",
            style = MaterialTheme.typography.bodySmall.copy(color = GrayTextColor)
        )
    }
}