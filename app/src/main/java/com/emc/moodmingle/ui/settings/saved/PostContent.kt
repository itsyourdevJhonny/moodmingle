package com.emc.moodmingle.ui.settings.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.ui.post.AudioThumbnail
import com.emc.moodmingle.ui.post.ImageThumbnail
import com.emc.moodmingle.ui.post.VideoThumbnail
import com.emc.moodmingle.ui.post.detectMediaType
import com.emc.moodmingle.viewmodel.local.PostViewModel

@Composable
fun PostContent(post: PostEntityFirebase) {
    val context = LocalContext.current
    val postViewModel = hiltViewModel<PostViewModel>()

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        LazyRow(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            itemsIndexed(
                items = post.urls.take(6),
                key = { _, url -> url }
            ) { index, url ->
                val mediaType = detectMediaType(url)

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when (mediaType) {
                        "image" -> {
                            ImageThumbnail(url, context)
                        }

                        "video" -> {
                            VideoThumbnail(url, postViewModel)
                        }

                        "audio" -> {
                            AudioThumbnail()
                        }
                    }

                    if (index == 5) {
                        if (post.urls.size > 6) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        Color.Black.copy(alpha = 0.5f),
                                        RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Text(
                                    text = "+ ${post.urls.size - 6}",
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}