package com.emc.moodmingle.ui.dailymood.hashtag

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.ui.create.post.hashtag.extractHashtags
import com.emc.moodmingle.ui.theme.HashtagTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark

@Composable
fun DailyMoodHashtagSection(mood: DailyMoodEntity) {
    AnimatedVisibility(
        visible = mood.text.hashtag.isNotBlank() && mood.text.hashtag != "#",
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.padding(start = 16.dp)
    ) {
        val hashtags = extractHashtags(mood.text.hashtag)
        Row {
            HashtagButton()
            TotalHashtag(size = hashtags.size)
        }
    }
}

@Composable
private fun HashtagButton() {
    IconButton(
        onClick = {},
        colors = IconButtonDefaults.iconButtonColors(containerColor = SecondaryDark)
    ) {
        Icon(
            painter = painterResource(R.drawable.hashtag),
            contentDescription = null,
            tint = HashtagTextColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun TotalHashtag(size: Int) {
    Text(
        text = "$size",
        color = Color.White,
        modifier = Modifier
            .offset(x = (-10).dp)
            .background(HashtagTextColor, CircleShape)
            .padding(horizontal = 6.dp)
    )
}