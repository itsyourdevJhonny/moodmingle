package com.emc.moodmingle.ui.post.audio

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.create.AllMediaGallery
import com.emc.moodmingle.ui.create.getFileName
import com.emc.moodmingle.ui.post.action.formatText
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient

@Composable
fun FilePicker(onSelectedType: (String) -> Unit, onUploadedUri: (List<Uri?>) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(top = 23.dp)
            .background(BrushPrimaryGradient, RoundedCornerShape(8.dp))
            .clickable { showDialog = true },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val uploadMedia = listOf(
            R.drawable.image to "Image",
            R.drawable.video to "Video",
            R.drawable.audio to "Audio"
        )

        uploadMedia.forEach { upload ->
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(upload.first),
                    contentDescription = upload.second,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            if (upload.second != "Audio") {
                Text(text = "/")
            }
        }
    }

    if (showDialog) {
        AllMediaGallery(
            onSelectedType = onSelectedType,
            onShowDialog = { showDialog = it },
            onUploadedUri = onUploadedUri
        )
    }
}

@Composable
fun AudioItemMini(
    uri: Uri,
    isPlaying: Boolean,
    onClickPlay: () -> Unit,
    onClickPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .background(BrushPrimaryGradient, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp)
            .size(80.dp)
    ) {
        val iconRes = if (isPlaying) R.drawable.pause else R.drawable.play
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(20.dp)
                .clickable {
                    if (isPlaying) onClickPause() else onClickPlay()
                }
        )

        Text(
            text = formatText(getFileName(context, uri), 6),
            color = Color.White,
            fontSize = 8.sp,
            maxLines = 1
        )
    }
}
