package com.emc.moodmingle.ui.create.util

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.modifier.drawGradient
import kotlinx.coroutines.delay

@Composable
fun ImageGallery(
    uris: List<Uri>,
    selectMultiple: Boolean = true,
    minSize: Dp = 120.dp,
    onUrisSelected: (List<Uri>) -> Unit,
) {
    val context = LocalContext.current

    var allImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val gridState = rememberLazyGridState()
    var isLoadingMore by remember { mutableStateOf(true) }
    var pageSize by remember { mutableIntStateOf(35) }

    val permissionLauncher = rememberMediaPermissionLauncher(
        onGranted = {},
        onDenied = { isLoadingMore = false }
    )

    LaunchedEffect(Unit) {
        handlePermissionAndLoad(
            context = context,
            permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) READ_MEDIA_IMAGES else READ_EXTERNAL_STORAGE,
            permissionLauncher = permissionLauncher,
            loader = { loadDeviceImages(context) },
            onLoaded = { images ->
                allImageUris = images
                isLoadingMore = false
            },
            onLoadingState = { isLoadingMore = it }
        )
    }

    LaunchedEffect(gridState, allImageUris, pageSize) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null &&
                    lastVisibleIndex >= pageSize - 3 &&
                    !isLoadingMore &&
                    pageSize < allImageUris.size
                ) {
                    isLoadingMore = true
                    delay(300)
                    pageSize += 35
                    isLoadingMore = false
                }
            }
    }

    val pagedImageUris = allImageUris.take(pageSize)

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Adaptive(minSize = minSize),
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryDark),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(pagedImageUris) { uri ->
            val isSelected = uris.contains(uri)

            Box(
                modifier = Modifier
                    .height(minSize)
                    .clickable {
                        if (selectMultiple) {
                            val newSelection = if (isSelected) uris - uri else uris + uri
                            onUrisSelected(newSelection)
                        } else {
                            onUrisSelected(if (isSelected) emptyList() else listOf(uri))
                        }
                    }
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = uri),
                    contentDescription = "Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                SelectionOverlay(isSelected = isSelected)
            }
        }

        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawGradient()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                    content = { CircularProgressIndicator() }
                )
            }
        }
    }
}
