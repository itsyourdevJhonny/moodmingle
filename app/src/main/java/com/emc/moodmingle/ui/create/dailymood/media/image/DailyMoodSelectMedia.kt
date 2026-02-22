package com.emc.moodmingle.ui.create.dailymood.media.image

import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.create.util.ImageGallery
import com.emc.moodmingle.ui.create.util.RemoveAllIcon
import com.emc.moodmingle.ui.create.util.RemoveSingleIcon
import com.emc.moodmingle.ui.create.util.VideoGallery
import com.emc.moodmingle.ui.create.util.VideoThumbnail
import com.emc.moodmingle.ui.create.util.countMediaTypes
import com.emc.moodmingle.ui.create.util.getMimeType
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.ScaffoldHeader
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun DailyMoodSelectMedia(
    uris: List<Uri>,
    selectMultiple: Boolean,
    onUrisSelected: (List<Uri>) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedUris by remember { mutableStateOf(uris) }

    BackHandler { onDismiss() }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            ScaffoldHeader(
                title = "Select Image/Video",
                enabled = selectedUris.isNotEmpty(),
                onDone = { onUrisSelected(selectedUris); onDismiss() },
                onBack = onDismiss
            )
        }
    ) { paddingValues ->
        Content(paddingValues, selectedUris, selectMultiple) { selectedUris = it }
    }
}

@Composable
fun Content(
    paddingValues: PaddingValues,
    uris: List<Uri>,
    selectMultiple: Boolean,
    onUrisSelected: (List<Uri>) -> Unit
) {
    val context = LocalContext.current

    var selectedIndex by remember { mutableIntStateOf(0) }
    var selectedType by remember { mutableStateOf("Image") }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(if (uris.isEmpty()) 0.dp else 12.dp)
    ) {
        MediaTypeTabs(selectedIndex, onTabSelected = { selectedIndex = it }) { selectedType = it }

        if (uris.isNotEmpty()) {
            Selected(context, uris)

            MediaThumbnailPreview(context, uris, selectMultiple, onUrisSelected)
        }

        Column {
            HorizontalDivider(thickness = 0.5.dp)

            if (selectedType.isNotBlank()) {
                DisplayGallery(selectedType, uris, selectMultiple, onUrisSelected)
            }
        }
    }
}

@Composable
private fun ColumnScope.MediaThumbnailPreview(
    context: Context,
    uris: List<Uri>,
    selectMultiple: Boolean,
    onUrisSelected: (List<Uri>) -> Unit
) {
    val itemVisibility = remember { mutableStateMapOf<Uri, Boolean>() }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (selectMultiple) Arrangement.spacedBy(8.dp) else Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        uris.forEachIndexed { index, uri ->
            item(uri.toString()) {
                val visible = itemVisibility.getOrPut(uri) { false }

                LaunchedEffect(uri) { if (!visible) itemVisibility[uri] = true }

                AnimatedVisibility(
                    visible = itemVisibility[uri] == true,
                    enter = fadeIn(animationSpec = tween(1000)) + scaleIn(),
                    exit = fadeOut(animationSpec = tween(1000)) + scaleOut()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(start = if (index == 0) 12.dp else Dp.Unspecified)
                            .size(if (selectMultiple) 80.dp else 180.dp)
                    ) {
                        val mimeType = getMimeType(context, uri) ?: ""

                        when {
                            mimeType.startsWith("image") -> ImageThumbnail(uri)
                            mimeType.startsWith("video") -> VideoThumbnail(videoUri = uri)
                        }

                        RemoveSingleIcon(itemVisibility, uri, uris, onUrisSelected)
                    }
                }
            }
        }

        if (uris.isNotEmpty() && uris.size > 1) {
            item("remove_all") {
                Box(Modifier.padding(end = 12.dp)) {
                    RemoveAllIcon(uris, itemVisibility, onUrisSelected)
                }
            }
        }
    }
}

@Composable
fun ImageThumbnail(uri: Uri) {
    Image(
        painter = rememberAsyncImagePainter(uri),
        contentDescription = "Selected image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
    )
}

@Composable
private fun MediaTypeTabs(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    onTypeSelected: (String) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedIndex,
        indicator = { tabPositions ->
            if (selectedIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    height = 1.dp,
                    color = Color.White
                )
            }
        }
    ) {
        listOf(
            "Image" to R.drawable.image,
            "Video" to R.drawable.video
        ).forEachIndexed { index, (type, icon) ->
            val isSelected = selectedIndex == index

            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.15f else 1f,
                animationSpec = tween(250)
            )

            val alpha by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.6f,
                animationSpec = tween(250)
            )

            Tab(
                selected = isSelected,
                onClick = { onTabSelected(index); onTypeSelected(type) },
                selectedContentColor = Color.White,
                modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale, alpha = alpha)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = type,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )

                    Text(text = type)
                }
            }
        }
    }
}

@Composable
private fun Selected(context: Context, uris: List<Uri> = emptyList()) {
    val (images, videos, _) = countMediaTypes(context, uris)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                modifier = Modifier
                    .size(18.dp)
                    .drawGradient()
            )
            Text(
                text = "Selected (${uris.size})",
                style = Typography.bodyMedium.copy(color = Color.White)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Count(images, "Image")
            Count(videos, "Video")
        }
    }
}

@Composable
private fun Count(count: Int, type: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            painter = painterResource(if (type == "Image") R.drawable.image else R.drawable.video),
            contentDescription = type,
            modifier = Modifier
                .size(14.dp)
                .drawGradient()
        )

        Text(text = "$type ($count)", style = Typography.bodySmall.copy(color = GrayTextColor))
    }
}

@Composable
private fun DisplayGallery(
    type: String,
    uris: List<Uri>,
    selectMultiple: Boolean,
    onUrisSelected: (List<Uri>) -> Unit
) {
    if (type == "Image") ImageGallery(uris, selectMultiple, minSize = 80.dp, onUrisSelected)
    else VideoGallery(uris, selectMultiple, minSize = 80.dp, onUrisSelected)
}