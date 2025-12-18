package com.emc.moodmingle.ui.post.action.more

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun InterestAndShareWithOther() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CardButton(text = "Interested", iconRes = R.drawable.interested) { }
        CardButton(text = "Uninterested", iconRes = R.drawable.not_interested) { }
        CardButton(text = "Share", iconRes = R.drawable.share_other) {}
    }
}

@Composable
fun CardButton(text: String, @DrawableRes iconRes: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(60.dp)
            .height(60.dp)
            .background(PrimaryDark, CircleShape)
            .border(
                width = 0.5.dp,
                brush = BrushPrimaryGradient,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier
                .size(32.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawGradient()
        )
    }
}