package com.emc.moodmingle.ui.create

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun rememberMediaPermissionLauncher(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): ManagedActivityResultLauncher<String, Boolean> {
    val context = LocalContext.current

    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onGranted()
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            onDenied()
        }
    }
}

suspend fun handlePermissionAndLoad(
    context: Context,
    permission: String,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    loader: suspend () -> List<Uri>,
    onLoaded: (List<Uri>) -> Unit,
    onLoadingState: (Boolean) -> Unit,
) {
    onLoadingState(true)

    val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    if (!isGranted) {
        permissionLauncher.launch(permission)
        onLoadingState(false)
        return
    }

    try {
        val data = loader()
        onLoaded(data)
    } finally {
        onLoadingState(false)
    }
}

suspend fun loadDeviceImages(context: Context): List<Uri> =
    withContext(Dispatchers.IO) {

        val imageList = mutableListOf<Uri>()
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Images.Media._ID
        )

        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            MediaStore.Images.Media.DATE_ADDED + " DESC"
        )?.use { cursor ->

            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val uri = Uri.withAppendedPath(collection, id.toString())
                imageList.add(uri)
            }
        }

        imageList
    }

suspend fun loadDeviceVideos(context: Context): List<Uri> =
    withContext(Dispatchers.IO) {

        val videoList = mutableListOf<Uri>()
        val collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Video.Media._ID
        )

        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            MediaStore.Video.Media.DATE_ADDED + " DESC"
        )?.use { cursor ->

            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val uri = Uri.withAppendedPath(collection, id.toString())
                videoList.add(uri)
            }
        }

        videoList
    }

suspend fun loadDeviceAudio(context: Context): List<Uri> =
    withContext(Dispatchers.IO) {

        val audioList = mutableListOf<Uri>()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID
        )

        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC"
        )?.use { cursor ->

            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val uri = Uri.withAppendedPath(collection, id.toString())
                audioList.add(uri)
            }
        }

        audioList
    }

@Composable
fun SelectionOverlay(isSelected: Boolean) {
    AnimatedVisibility(
        visible = isSelected,
        enter = fadeIn(animationSpec = tween(200)),
        exit = fadeOut(animationSpec = tween(200))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.graphicsLayer(alpha = 0.99f)
            )
        }
    }
}

fun Modifier.detectLongPress(
    onLongPress: () -> Unit,
    onTap: () -> Unit
): Modifier {
    return pointerInput(Unit) {
        detectTapGestures(
            onLongPress = { onLongPress() },
            onTap = { onTap() }
        )
    }
}
