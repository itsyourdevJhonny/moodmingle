package com.emc.moodmingle.ui.settings.personal

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.modifier.drawGradient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ImageGallery(onSelectedImage: (Uri?, Boolean) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val permission = remember { getImagePermission() }
    val permissionLauncher = rememberPermissionLauncher(
        context = context,
        scope = scope,
        onImagesLoaded = { uris ->
            imageUris = uris
            isLoading = false
        },
        onDenied = { isLoading = false }
    )

    // automatically request permission and load images
    LaunchedEffect(Unit) {
        handlePermissionAndLoadImages(
            context = context,
            permission = permission,
            permissionLauncher = permissionLauncher,
            onImagesLoaded = {
                imageUris = it
                isLoading = false
            },
            onLoading = { isLoading = it }
        )
    }

    if (isLoading) {
        LoadingIndicator()
    } else {
        ImageGrid(
            imageUris = imageUris,
            selectedImage = selectedImage,
            onImageClick = { uri ->
                selectedImage = if (selectedImage == uri) null else uri
                onSelectedImage(selectedImage, true)
            }
        )
    }
}


/* composable for loading indicator */
@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryDark),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(48.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawGradient(),
            strokeWidth = 4.dp
        )
    }
}

/* composable for displaying image grid */
@Composable
private fun ImageGrid(
    imageUris: List<Uri>,
    selectedImage: Uri?,
    onImageClick: (Uri) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(BrushPrimaryGradient, alpha = 0.6f)
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(imageUris) { uri ->
            val isSelected = uri == selectedImage
            ImageItem(uri, isSelected, onImageClick)
        }
    }
}

@Composable
private fun ImageItem(uri: Uri, isSelected: Boolean, onClick: (Uri) -> Unit) {
    Box {
        AsyncImage(
            model = uri,
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(1f)
                .clickable { onClick(uri) },
            contentScale = ContentScale.Crop
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            )
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(6.dp)
                    .size(32.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(
                                brush = BrushPrimaryGradient,
                                blendMode = BlendMode.SrcAtop
                            )
                        }
                    }
            )
        }
    }
}


/* function to determine correct image permission */
private fun getImagePermission(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        Manifest.permission.READ_MEDIA_IMAGES
    else
        Manifest.permission.READ_EXTERNAL_STORAGE
}

/* composable that creates a permission launcher */
@Composable
private fun rememberPermissionLauncher(
    context: Context,
    scope: CoroutineScope,
    onImagesLoaded: (List<Uri>) -> Unit,
    onDenied: () -> Unit
): ManagedActivityResultLauncher<String, Boolean> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            scope.launch {
                val images = loadDeviceImages(context)
                onImagesLoaded(images)
            }
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            onDenied()
        }
    }
}

/* function to handle permission check and image loading */
private suspend fun handlePermissionAndLoadImages(
    context: Context,
    permission: String,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    onImagesLoaded: (List<Uri>) -> Unit,
    onLoading: (Boolean) -> Unit
) {
    val granted =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    if (granted) {
        onLoading(true)
        val images = loadDeviceImages(context)
        onImagesLoaded(images)
        onLoading(false)
    } else {
        permissionLauncher.launch(permission)
    }
}

//---------------------------------------
// ---------------- VIDEO GALLERY ----------------
@Composable
fun VideoGallery(onSelectedVideo: (Uri?, Boolean) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var videoUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedVideo by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        Manifest.permission.READ_MEDIA_VIDEO
    else Manifest.permission.READ_EXTERNAL_STORAGE

    val permissionLauncher = rememberPermissionLauncher(
        context = context,
        scope = scope,
        loadFunction = { loadDeviceVideos(context) },
        onMediaLoaded = { uris ->
            videoUris = uris
            isLoading = false
        },
        onDenied = { isLoading = false }
    )

    LaunchedEffect(Unit) {
        handlePermissionAndLoadMedia(
            context = context,
            permission = permission,
            permissionLauncher = permissionLauncher,
            loadFunction = { loadDeviceVideos(context) },
            onMediaLoaded = { videoUris = it; isLoading = false },
            onLoading = { isLoading = it }
        )
    }

    if (isLoading) LoadingIndicator()
    else MediaGrid(videoUris, selectedVideo) { uri ->
        selectedVideo = if (selectedVideo == uri) null else uri
        onSelectedVideo(selectedVideo, true)
    }
}

// ---------------- AUDIO GALLERY ----------------
@Composable
fun AudioGallery(onSelectedAudio: (Uri?, Boolean) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var audioUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedAudio by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        Manifest.permission.READ_MEDIA_AUDIO
    else Manifest.permission.READ_EXTERNAL_STORAGE

    val permissionLauncher = rememberPermissionLauncher(
        context = context,
        scope = scope,
        loadFunction = { loadDeviceAudios(context) },
        onMediaLoaded = { uris ->
            audioUris = uris
            isLoading = false
        },
        onDenied = { isLoading = false }
    )

    LaunchedEffect(Unit) {
        handlePermissionAndLoadMedia(
            context = context,
            permission = permission,
            permissionLauncher = permissionLauncher,
            loadFunction = { loadDeviceAudios(context) },
            onMediaLoaded = { audioUris = it; isLoading = false },
            onLoading = { isLoading = it }
        )
    }

    if (isLoading) LoadingIndicator()
    else MediaGrid(audioUris, selectedAudio) { uri ->
        selectedAudio = if (selectedAudio == uri) null else uri
        onSelectedAudio(selectedAudio, true)
    }
}

// ---------------- GRID SHARED FUNCTION ----------------
@Composable
private fun MediaGrid(
    uris: List<Uri>,
    selectedUri: Uri?,
    onItemClick: (Uri) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(uris) { uri ->
            val isSelected = uri == selectedUri
            Box {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onItemClick(uri) },
                    contentScale = ContentScale.Crop
                )
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                    )
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(6.dp)
                            .size(32.dp)
                            .graphicsLayer(alpha = 0.99f)
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(
                                        brush = BrushPrimaryGradient,
                                        blendMode = BlendMode.SrcAtop
                                    )
                                }
                            }
                    )
                }
            }
        }
    }
}


// ---------------- PERMISSION HANDLING ----------------
@Composable
private fun rememberPermissionLauncher(
    context: Context,
    scope: CoroutineScope,
    loadFunction: suspend () -> List<Uri?>,
    onMediaLoaded: (List<Uri>) -> Unit,
    onDenied: () -> Unit
): ManagedActivityResultLauncher<String, Boolean> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            scope.launch {
                val media = loadFunction().filterNotNull()
                onMediaLoaded(media)
            }
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            onDenied()
        }
    }
}

private suspend fun handlePermissionAndLoadMedia(
    context: Context,
    permission: String,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    loadFunction: suspend () -> List<Uri?>,
    onMediaLoaded: (List<Uri>) -> Unit,
    onLoading: (Boolean) -> Unit
) {
    val granted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    if (granted) {
        onLoading(true)
        val media = loadFunction().filterNotNull()
        onMediaLoaded(media)
        onLoading(false)
    } else {
        permissionLauncher.launch(permission)
    }
}