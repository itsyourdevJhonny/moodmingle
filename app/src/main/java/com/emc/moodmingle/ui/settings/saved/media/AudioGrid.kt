package com.emc.moodmingle.ui.settings.saved.media

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.normal.PostEntityFirebase
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.domain.remote.model.saved.SaveEntityFirebase
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun AudioGrid(
    save: SaveEntityFirebase,
    post: PostEntityFirebase,
    user: UserEntityFirebase,
    onShowSheet: (Boolean) -> Unit
) {
    val context = LocalContext.current

    PostInformation(save, post, user, onShowSheet = onShowSheet)

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp, max = 600.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(post.urls) { url ->
            if (isAudio(url)) {
                var durationText by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    extractDuration(
                        context,
                        url,
                        durationText,
                        onDurationText = { durationText = it }
                    )
                }

                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.music_disk),
                        contentDescription = "Audio",
                        modifier = Modifier
                            .graphicsLayer(alpha = 0.99f)
                            .drawGradient()
                    )

                    if (durationText.isEmpty()) {
                        CircularProgressIndicator(
                            strokeWidth = 1.dp,
                            modifier = Modifier.size(16.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = durationText,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

fun isAudio(url: String): Boolean {
    return url.endsWith("mp3")
            || url.endsWith("wav")
            || url.endsWith("aac")
            || url.endsWith("ogg")
            || url.endsWith("avi")
            || url.endsWith("flv")
}