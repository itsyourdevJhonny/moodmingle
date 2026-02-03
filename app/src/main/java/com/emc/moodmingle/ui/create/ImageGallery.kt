package com.emc.moodmingle.ui.create

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient

@Composable
fun ImageGallery(selectedImages: List<Uri>, onSelectedImage: (List<Uri>) -> Unit) {
    val context = LocalContext.current
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val permissionLauncher = rememberMediaPermissionLauncher(
        onGranted = {},
        onDenied = { isLoading = false }
    )

    LaunchedEffect(Unit) {
        handlePermissionAndLoad(
            context = context,
            permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) READ_MEDIA_IMAGES else READ_EXTERNAL_STORAGE,
            permissionLauncher = permissionLauncher,
            loader = { loadDeviceImages(context) },
            onLoaded = {
                imageUris = it
                isLoading = false
            },
            onLoadingState = { isLoading = it }
        )
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(BrushPrimaryGradient, alpha = 0.6f)
                .padding(top = 2.dp, start = 2.dp, end = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(imageUris) { uri ->
                val isSelected = selectedImages.contains(uri)
                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .clickable {
                            val newSelection = if (isSelected) {
                                selectedImages - uri
                            } else {
                                selectedImages + uri
                            }
                            onSelectedImage(newSelection)
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
        }
    }
}
