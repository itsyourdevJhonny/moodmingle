package com.emc.moodmingle.ui.chat.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable

@Composable
fun AnimatedReplyContainer(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 250, easing = LinearOutSlowInEasing)
        ) + slideInVertically(
            initialOffsetY = { it / 3 }
        ),
        exit = fadeOut() + slideOutVertically(
            targetOffsetY = { it / 3 }
        )
    ) {
        content()
    }
}
