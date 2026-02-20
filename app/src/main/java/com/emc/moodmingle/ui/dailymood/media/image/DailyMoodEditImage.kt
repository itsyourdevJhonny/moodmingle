package com.emc.moodmingle.ui.dailymood.media.image

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.emc.moodmingle.domain.remote.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.domain.remote.model.post.dailymood.media.ShapeType
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.ScaffoldHeader
import com.emc.moodmingle.utils.media.image.ImageFilterType
import com.emc.moodmingle.utils.media.image.ImageFilters
import com.emc.moodmingle.utils.text.toColorFilter
import kotlinx.coroutines.delay

@Composable
fun DailyMoodEditImage(
    mood: DailyMoodEntity,
    onEdited: (DailyMoodEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedType by remember { mutableStateOf(ImageFilterType.valueOf(mood.media.image.filterName)) }
    var isSelected by remember { mutableStateOf(false) }

    LaunchedEffect(selectedType) {
        isSelected = true
        delay(1000)
        isSelected = false
    }

    BackHandler { onDismiss() }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            ScaffoldHeader(
                title = "Edit Image",
                enabled = selectedType != ImageFilterType.NORMAL || mood.media.image.shapeType != ShapeType.NORMAL,
                onDone = {
                    onEdited(
                        mood.copy(media = mood.media.copy(image = mood.media.image.copy(filterName = selectedType.name)))
                    )
                    onDismiss()
                },
                onBack = onDismiss
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = { DailyMoodEditImageShape(mood, onEdited) },
        bottomBar = { Footer(mood, selectedType) { selectedType = it } }
    ) { paddingValues ->
        Content(paddingValues, mood, selectedType, isSelected)
    }
}

@Composable
private fun Footer(
    mood: DailyMoodEntity,
    selectedType: ImageFilterType,
    onTypeSelected: (ImageFilterType) -> Unit,
) {
    BottomAppBar(
        containerColor = PrimaryDark,
        modifier = Modifier
            .height(180.dp)
            .clip(RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp))
    ) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(ImageFilterType.entries) { index, type ->
                val isSelected = selectedType == type

                Column(
                    modifier = Modifier
                        .padding(
                            start = if (index == 0) 8.dp else Dp.Unspecified,
                            end = if (index == ImageFilterType.entries.size - 1) 8.dp else Dp.Unspecified
                        )
                        .border(
                            width = if (isSelected) 2.dp else 0.dp,
                            brush = if (isSelected) BrushPrimaryGradient else SolidColor(Color.Transparent),
                            shape = RoundedCornerShape(if (isSelected) 8.dp else 0.dp)
                        )
                        .background(
                            brush = if (isSelected) BrushPrimaryGradient else SolidColor(Color.Transparent),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FilterOutput(mood, type, onTypeSelected)
                    FilterName(type)
                }
            }
        }
    }
}

@Composable
private fun FilterOutput(
    mood: DailyMoodEntity,
    type: ImageFilterType,
    onTypeSelected: (ImageFilterType) -> Unit,
) {
    AsyncImage(
        model = mood.media.urls[0],
        contentDescription = null,
        contentScale = ContentScale.Crop,
        colorFilter = type.name.toColorFilter(),
        modifier = Modifier
            .clickable { onTypeSelected(type) }
            .size(100.dp)
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
    )
}

@Composable
private fun FilterName(type: ImageFilterType) {
    Text(
        text = type.name.lowercase().replaceFirstChar { it.uppercase() },
        style = Typography.bodyMedium,
        color = Color.White
    )
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    mood: DailyMoodEntity,
    selectedType: ImageFilterType,
    isSelected: Boolean,
) {
    val isShapeNormal = mood.media.image.shapeType == ShapeType.NORMAL

    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = mood.media.urls[0],
            contentDescription = null,
            contentScale = if (isShapeNormal) ContentScale.Fit else ContentScale.Crop,
            colorFilter = ColorFilter.colorMatrix(ImageFilters.matrix(selectedType)),
            modifier = Modifier
                .size(if (isShapeNormal) Dp.Unspecified else 260.dp)
                .clip(animatedShape(mood.media.image.shapeType))
        )

        AnimatedVisibility(visible = isSelected, enter = fadeIn(), exit = fadeOut()) {
            Text(
                text = selectedType.name.lowercase().replaceFirstChar { it.uppercase() },
                color = Color.White
            )
        }
    }
}
