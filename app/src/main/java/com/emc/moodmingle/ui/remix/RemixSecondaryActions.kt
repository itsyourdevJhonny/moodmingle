package com.emc.moodmingle.ui.remix

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.utils.modifier.grayCircleBorder

@Composable
fun BoxScope.RemixSecondaryActions(
    isHidden: Boolean,
    onShowColorPicker: (Boolean) -> Unit,
    onShowFontPicker: (Boolean) -> Unit,
    onShowAlignPicker: (Boolean) -> Unit
) {
    AnimatedVisibility(
        visible = !isHidden,
        enter = expandHorizontally(initialWidth = { maxWidth -> maxWidth / 100 }),
        exit = fadeOut(),
        modifier = Modifier.align(Alignment.BottomStart)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                R.drawable.paint,
                R.drawable.font,
                R.drawable.text_align_start
            ).forEach { icon ->
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color.Black.copy(0.4f), CircleShape)
                        .grayCircleBorder()
                        .clickable {
                            when (icon) {
                                R.drawable.paint -> onShowColorPicker(true)
                                R.drawable.font -> onShowFontPicker(true)
                                R.drawable.text_align_start -> onShowAlignPicker(true)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = "Action",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}