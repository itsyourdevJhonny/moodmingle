package com.emc.moodmingle.ui.create.dailymood.action

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.dailymood.DailyMoodEntity

@Composable
fun DailyMoodFloatingActions(
    mood: DailyMoodEntity,
    selectedAction: String,
    onActionSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PrimaryActions(mood, selectedAction, onActionSelected)
        HideIcon(selectedAction, onActionSelected)
    }
}

@Composable
private fun PrimaryActions(
    mood: DailyMoodEntity,
    selectedAction: String,
    onActionSelected: (String) -> Unit
) {
    AnimatedVisibility(
        visible = selectedAction != "hide",
        enter = slideInVertically(initialOffsetY = { maxHeight -> maxHeight / 100 }),
        exit = slideOutVertically(targetOffsetY = { maxHeight -> maxHeight / 100 })
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            getActions().forEach { (label, icon) ->
                ActionIcon(mood, label, icon, onActionSelected)
            }
        }
    }
}

@Composable
private fun ActionIcon(
    dailyMood: DailyMoodEntity,
    label: String,
    icon: Int,
    onActionSelected: (String) -> Unit,
) {
    IconButton(
        onClick = { onActionSelected(label) },
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.Black.copy(alpha = 0.3f),
            contentColor = Color.White
        )
    ) {
        when {
            label == "mood" && dailyMood.mood.description.isNotBlank() -> {
                Text(text = dailyMood.mood.emoji, fontSize = 28.sp)
            }

            label == "music" && dailyMood.musicTrack != null -> {
                AsyncImage(
                    model = dailyMood.musicTrack.streamUrl,
                    contentDescription = "Music",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                )
            }

            else -> {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = "Action",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun HideIcon(selectedAction: String, onActionSelected: (String) -> Unit) {
    val rotation by animateFloatAsState(
        targetValue = if (selectedAction == "hide") 270f else 0f,
        label = "icon_rotation"
    )

    IconButton(
        onClick = { onActionSelected(if (selectedAction == "hide") "" else "hide") },
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.Black.copy(alpha = 0.3f),
            contentColor = Color.White
        )
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "Action",
            tint = Color.White,
            modifier = Modifier
                .size(38.dp)
                .rotate(rotation)
        )
    }
}

private fun getActions(): List<Pair<String, Int>> {
    return listOf(
        "mood" to R.drawable.mood,
        "text" to R.drawable.text_style,
        "media" to R.drawable.image_video,
        "gif" to R.drawable.gif,
        "music" to R.drawable.music_note,
        "location" to R.drawable.location,
    )
}