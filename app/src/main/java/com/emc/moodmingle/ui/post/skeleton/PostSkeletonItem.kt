package com.emc.moodmingle.ui.post.skeleton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient

@Composable
fun PostSkeletonItem() {
    val shimmerBrush = ShimmerAnimation()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
            .background(shimmerBrush)
    ) {
        Icon(
            painter = painterResource(R.drawable.video),
            modifier = Modifier
                .align(Alignment.Center)
                .size(60.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush = shimmerBrush,
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                },
            contentDescription = "Video"
        )
    }
}
