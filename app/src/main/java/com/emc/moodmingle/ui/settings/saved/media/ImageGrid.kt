package com.emc.moodmingle.ui.settings.saved.media

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.emc.moodmingle.domain.remote.model.post.normal.PostEntityFirebase
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.domain.remote.model.saved.SaveEntityFirebase

@Composable
fun ImageGrid(
    save: SaveEntityFirebase,
    post: PostEntityFirebase,
    user: UserEntityFirebase,
    onShowSheet: (Boolean) -> Unit
) {
    PostInformation(save, post, user, onShowSheet = onShowSheet)

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp, max = 600.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(post.urls) { url ->
            if (getMime(url).startsWith("image")) {
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun isImage(url: String): Boolean {
    return url.endsWith("jpeg")
            || url.endsWith("jpg")
            || url.endsWith("png")
            || url.endsWith("webp")
            || url.endsWith("bmp")
            || url.endsWith("tiff")
}