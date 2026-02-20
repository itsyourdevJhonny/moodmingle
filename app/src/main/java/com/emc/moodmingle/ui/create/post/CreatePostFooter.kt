package com.emc.moodmingle.ui.create.post

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.normal.NormalPostEntity
import com.emc.moodmingle.ui.theme.PrimaryDark

@Composable
fun CreatePostFooter(
    post: NormalPostEntity,
    onTypeSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(bottom = 42.dp)
            .fillMaxWidth()
            .background(Color.Black)
    ) {
        CreatePostActions(post, onTypeSelected)
    }
}

@Composable
private fun CreatePostActions(post: NormalPostEntity, onTypeSelected: (String) -> Unit) {
    val actionTypes = getActionTypes()

    AnimatedVisibility(
        visible = post.description.text.isNotEmpty() || post.mood.emoji.isNotEmpty() || post.urls.isNotEmpty(),
        enter = expandHorizontally(initialWidth = { maxWidth -> maxWidth / 100 }),
        exit = fadeOut()
    ) {
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            items(actionTypes) { (icon, label) ->
                Box(
                    modifier = Modifier
                        .padding(
                            start = if (icon == R.drawable.mood) 16.dp else Dp.Unspecified,
                            end = if (icon == R.drawable.event) 16.dp else Dp.Unspecified
                        )
                        .size(48.dp)
                        .background(PrimaryDark, CircleShape)
                        .clickable { onTypeSelected(label) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = "Action",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

private fun getActionTypes(): List<ActionType> {
    return listOf(
        ActionType(R.drawable.mood, "mood"),
        ActionType(R.drawable.text_style, "text"),
        ActionType(R.drawable.image_video, "media"),
        ActionType(R.drawable.music_note, "music"),
        ActionType(R.drawable.hashtag, "hashtag"),
        ActionType(R.drawable.mention, "mention"),
        ActionType(R.drawable.tag_people, "tag"),
        ActionType(R.drawable.location, "location"),
        ActionType(R.drawable.event, "event")
    )
}

private data class ActionType(
    val icon: Int,
    val label: String
)