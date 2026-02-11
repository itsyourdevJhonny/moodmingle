package com.emc.moodmingle.utils.media.video.editor

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.create.retrieveVideoFrame

@Composable
fun EditVideoRotation(
    videoUri: Uri,
    state: VideoEditorState,
    onStateChanged: (VideoEditorState) -> Unit,
) {
    val context = LocalContext.current

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        listOf(360f, 90f, 180f, 270f).forEach { degrees ->
            var bitmap by remember { mutableStateOf<Bitmap?>(null) }

            LaunchedEffect(Unit) {
                bitmap = retrieveVideoFrame(context, videoUri)
            }

            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .rotate(degrees)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onStateChanged(state.copy(rotation = degrees)) }
                )
            }
        }
    }
}