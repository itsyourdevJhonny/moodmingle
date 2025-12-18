package com.emc.moodmingle.ui.settings.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import com.emc.moodmingle.ui.theme.SecondaryDark

@Composable
fun FavoriteSkeletonCard() {
    val shimmer = ShimmerAnimation()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .height(164.dp)
            .background(SecondaryDark, RoundedCornerShape(8.dp))
            .clickable {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(shimmer, CircleShape)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(10.dp)
                                .background(shimmer)
                        )

                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(10.dp)
                                .background(shimmer)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(8.dp)
                        .background(shimmer)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 4.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(12.dp)
                                    .background(shimmer)
                            )
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .heightIn(8.dp)
                                    .background(shimmer)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(3) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(shimmer)
                            )
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(shimmer)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(3) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(shimmer)
                            )
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(shimmer)
                            )
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .background(shimmer)
            )
        }
    }
}