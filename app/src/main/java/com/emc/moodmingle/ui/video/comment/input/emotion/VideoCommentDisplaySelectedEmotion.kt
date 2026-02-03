package com.emc.moodmingle.ui.video.comment.input.emotion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.roundedGradientBorder

@Composable
fun VideoCommentDisplaySelectedEmotion(
    emotion: Pair<String, String>,
    onSelectedEmotion: (Pair<String, String>) -> Unit
) {
    AnimatedVisibility(
        visible = emotion.second.isNotBlank(),
        enter = slideInHorizontally(
            initialOffsetX = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .background(SecondaryDark, RoundedCornerShape(8.dp))
                    .roundedGradientBorder(8.dp),
                contentAlignment = Alignment.Center
            ) {
                EmotionIconAndText(emotion)
                RemoveEmotionIcon(onSelectedEmotion)
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun BoxScope.RemoveEmotionIcon(onSelectedEmotion: (Pair<String, String>) -> Unit) {
    Icon(
        painter = painterResource(R.drawable.remove),
        contentDescription = "Remove",
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .size(20.dp)
            .offset(x = 32.dp)
            .clickable { onSelectedEmotion("" to "") },
        tint = Color.Red
    )
}

@Composable
private fun EmotionIconAndText(emotion: Pair<String, String>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.feelings_colored),
            contentDescription = "Emotion Tag",
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = emotion.second,
            style = Typography.bodyLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
        )

        Text(text = emotion.first, color = Color.White)
    }
}