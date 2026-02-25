package com.emc.moodmingle.ui.video.comment.media

import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.create.util.AudioGallery
import com.emc.moodmingle.ui.create.util.ImageGallery
import com.emc.moodmingle.ui.create.util.VideoGallery
import com.emc.moodmingle.ui.create.util.VideoThumbnail
import com.emc.moodmingle.ui.create.util.countMediaTypes
import com.emc.moodmingle.ui.create.util.getMimeType
import com.emc.moodmingle.ui.post.audio.AudioItemMini
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.BackIcon
import com.emc.moodmingle.utils.modifier.drawGradient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VideoCommentSelectMedia(
    mediaUris: List<Uri>,
    selectedMediaType: String,
    isSelected: Boolean,
    onSelectedUris: (List<Uri>) -> Unit,
    onSelectedMediaType: (String) -> Unit,
    onSelected: (Boolean) -> Unit
) {
    Dialog(
        onDismissRequest = { onSelectedMediaType("") },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(modifier = Modifier.background(PrimaryDark)) {
            SelectMediaHeader(
                mediaUris,
                selectedMediaType,
                isSelected,
                onSelectedMediaType,
                onSelectedUris,
                onSelected
            )

            when (selectedMediaType) {
                "Image" -> ImageGallery(
                    uris = mediaUris,
                    onUrisSelected = onSelectedUris
                )

                "Video" -> VideoGallery(
                    uris = mediaUris,
                    onUrisSelected = onSelectedUris
                )

                "Audio" -> AudioGallery(
                    selectedAudios = mediaUris,
                    onSelectedAudio = onSelectedUris
                )
            }
        }
    }
}

@Composable
private fun SelectMediaHeader(
    mediaUris: List<Uri>,
    selectedMediaType: String,
    isSelected: Boolean,
    onSelectedMediaType: (String) -> Unit,
    onSelectedUris: (List<Uri>) -> Unit,
    onSelected: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackIconAndText(onSelectedMediaType, onSelectedUris, selectedMediaType, isSelected)
        SelectedMediaTextAndSize(selectedMediaType, mediaUris)
        VideoCommentSelectedMediaPreview(mediaUris, selectedMediaType, onSelectedUris)

        if (mediaUris.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .background(BrushPrimaryGradient, CircleShape)
                    .clickable {
                        onSelected(true); onSelectedMediaType("")
                    }
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.select_hand),
                        contentDescription = "Select",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )

                    Text(
                        text = "Select",
                        style = Typography.bodyLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun VideoCommentSelectedMediaPreview(
    mediaUris: List<Uri>,
    selectedMediaType: String = "",
    onSelectedUris: (List<Uri>) -> Unit
) {
    val context = LocalContext.current

    val itemVisibility = remember { mutableStateMapOf<Uri, Boolean>() }
    val mediaPlayers = remember { mutableStateMapOf<Uri, MediaPlayer>() }
    var currentlyPlaying by remember { mutableStateOf<Uri?>(null) }

    DisposableEffect(Unit) {
        onDispose { mediaPlayers.values.forEach { it.release() } }
    }

    LazyRow(
        modifier = Modifier
            .padding(start = 6.dp, top = 8.dp, bottom = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        mediaUris.forEach { uri ->
            item(uri.toString()) {
                val visible = itemVisibility.getOrPut(uri) { false }
                val mimeType = getMimeType(context, uri) ?: ""

                if (selectedMediaType.isNotEmpty()) {
                    if (mimeType.startsWith("image") && selectedMediaType != "Image") return@item
                    if (mimeType.startsWith("video") && selectedMediaType != "Video") return@item
                    if (mimeType.startsWith("audio") && selectedMediaType != "Audio") return@item
                }

                LaunchedEffect(uri) { if (!visible) itemVisibility[uri] = true }

                AnimatedVisibility(
                    visible = itemVisibility[uri] == true,
                    enter = fadeIn(animationSpec = tween(1000)) + scaleIn(),
                    exit = fadeOut(animationSpec = tween(1000)) + scaleOut()
                ) {
                    Box(modifier = Modifier.size(100.dp)) {
                        when {
                            mimeType.startsWith("image") -> {
                                Image(
                                    painter = rememberAsyncImagePainter(uri),
                                    contentDescription = "Selected image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(
                                            width = 0.5.dp,
                                            brush = BrushPrimaryGradient,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                )
                            }

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

                        RemoveSingleMediaIcon(itemVisibility, uri, onSelectedUris, mediaUris)
                    }
                }
            }
        }

        if (mediaUris.isNotEmpty() && mediaUris.size > 1) {
            item("remove_all") {
                RemoveAllMediaIcon(mediaUris, itemVisibility, onSelectedUris)
            }
        }
    }
}

@Composable
private fun RemoveAllMediaIcon(
    mediaUris: List<Uri>,
    itemVisibility: SnapshotStateMap<Uri, Boolean>,
    onSelectedUris: (List<Uri>) -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(Color.Red.copy(alpha = 0.8f), CircleShape)
            .clickable {
                mediaUris.forEach { uri -> itemVisibility[uri] = false }

                CoroutineScope(Dispatchers.IO).launch {
                    delay(300)
                    onSelectedUris(emptyList())
                    itemVisibility.clear()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Remove all",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun BoxScope.RemoveSingleMediaIcon(
    itemVisibility: SnapshotStateMap<Uri, Boolean>,
    uri: Uri,
    onSelectedUris: (List<Uri>) -> Unit,
    mediaUris: List<Uri>
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(Color.Red.copy(alpha = 0.6f), CircleShape)
            .align(Alignment.TopEnd)
            .clickable {
                itemVisibility[uri] = false
                CoroutineScope(Dispatchers.IO).launch {
                    delay(300)
                    onSelectedUris(mediaUris - uri)
                    itemVisibility.remove(uri)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Remove",
            tint = Color.White
        )
    }
}

@Composable
private fun SelectedMediaTextAndSize(selectedMediaType: String, mediaUris: List<Uri>) {
    val context = LocalContext.current
    val (images, videos, audios) = countMediaTypes(context, mediaUris)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(
                when (selectedMediaType) {
                    "Image" -> R.drawable.image
                    "Video" -> R.drawable.video
                    else -> R.drawable.audio
                }
            ),
            contentDescription = selectedMediaType,
            modifier = Modifier
                .size(20.dp)
                .drawGradient()
        )

        Text(
            text = "Selected $selectedMediaType${if (images > 1 || videos > 1 || audios > 1) "s" else ""}:",
            style = Typography.bodyLarge
        )

        Text(
            text = "${
                when (selectedMediaType) {
                    "Image" -> images
                    "Video" -> videos
                    else -> audios
                }
            }",
            style = Typography.bodyLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun BackIconAndText(
    onSelectedMediaType: (String) -> Unit,
    onSelectedUris: (List<Uri>) -> Unit,
    selectedMediaType: String,
    isSelected: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        BackIcon(
            onClick = {
                onSelectedMediaType("")

                if (!isSelected) {
                    onSelectedUris(emptyList())
                }
            }
        )

        Text(
            text = "Select $selectedMediaType",
            style = Typography.bodyLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
        )
    }
}