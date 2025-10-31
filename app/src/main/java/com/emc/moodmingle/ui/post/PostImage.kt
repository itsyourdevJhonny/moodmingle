package com.emc.moodmingle.ui.post

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.emc.moodmingle.ui.post.image.FullImageDialog
import com.emc.moodmingle.ui.post.image.ThumbnailImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostImage(thumbnailRes: Int, fullImageRes: Int) {
    var showFullImage by remember { mutableStateOf(false) }

    ThumbnailImage(thumbnailRes, onImageClick = { showFullImage = true })

    if (showFullImage) {
        FullImageDialog(fullImageRes, onDismiss = { showFullImage = false })
    }
}