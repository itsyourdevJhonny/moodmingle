package com.emc.moodmingle.ui.dailymood.media.image

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.data.firebase.model.post.dailymood.ShapeType
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient

@Composable
fun DailyMoodEditImageShape(
    dailyMood: DailyMoodEntity,
    onDailyMoodEdited: (DailyMoodEntity) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(ShapeType.entries) { index, shapeType ->
            val isSelected = shapeType == dailyMood.media.image.shapeType

            Box(
                modifier = Modifier
                    .padding(
                        start = if (index == 0) 16.dp else Dp.Unspecified,
                        end = if (index == ShapeType.entries.size - 1) 16.dp else Dp.Unspecified
                    )
            ) {
                AsyncImage(
                    model = dailyMood.media.urls.first(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(animatedShape(shapeType))
                        .border(
                            width = if (isSelected) 1.dp else 0.5.dp,
                            brush = if (isSelected) BrushPrimaryGradient else SolidColor(
                                Color.Black.copy(alpha = 0.3f)
                            ),
                            shape = animatedShape(shapeType)
                        )
                        .clickable {
                            onDailyMoodEdited(
                                dailyMood.copy(
                                    media = dailyMood.media.copy(
                                        image = dailyMood.media.image.copy(shapeType = shapeType)
                                    )
                                )
                            )
                        }
                )
            }
        }
    }
}