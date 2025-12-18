package com.emc.moodmingle.ui.settings.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.post.skeleton.ShimmerAnimation

@Composable
fun SavedSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(ShimmerAnimation(), CircleShape)
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .height(12.dp)
                            .width(48.dp)
                            .background(ShimmerAnimation(), CircleShape)
                    )

                    Box(
                        modifier = Modifier
                            .height(12.dp)
                            .width(48.dp)
                            .background(ShimmerAnimation(), CircleShape)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .height(12.dp)
                    .width(36.dp)
                    .background(ShimmerAnimation(), CircleShape)
                    .align(Alignment.CenterEnd)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(6) {
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .width(46.dp)
                        .background(ShimmerAnimation(), RoundedCornerShape(8.dp))
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Box(
                modifier = Modifier
                    .height(12.dp)
                    .width(36.dp)
                    .background(ShimmerAnimation(), CircleShape)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .height(12.dp)
                            .width(32.dp)
                            .background(ShimmerAnimation(), CircleShape)
                    )
                }
            }
        }
    }
}