package com.emc.moodmingle.ui.settings.personal

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.cloudinary.CloudinaryService
import com.emc.moodmingle.data.firebase.model.UserEntityFirebase
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.profile.DrawUserNoPaddingLine
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.viewmodel.local.UserViewModel
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AvatarPicture1(avatarUrl: String, userEntity: UserEntityFirebase) {
    val userViewModel = hiltViewModel<UserViewModel>()
    val firebaseUserViewModel = hiltViewModel<FirebaseUserViewModel>()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isShowDialog by remember { mutableStateOf(false) }
    var isUploaded by remember { mutableStateOf(false) }
    var isProfileUpdated by remember { mutableStateOf(false) }
    var isSelected by remember { mutableStateOf(false) }
    var fileUri by remember { mutableStateOf<Uri?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Avatar Picture",
            style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
        )

        // show avatar preview section
        AvatarPreviewSection(
            avatarUrl = avatarUrl,
            isProfileUpdated = isProfileUpdated,
            isUploaded = isUploaded,
            fileUri = fileUri,
            context = context,
            scope = scope,
            userEntity = userEntity,
            userViewModel = userViewModel,
            firebaseUserViewModel = firebaseUserViewModel,
            onUploadFinished = {
                isUploaded = false
                isProfileUpdated = true
            }
        )

        // button to open dialog
        AvatarChangeButton(
            isUploaded = isUploaded,
            onClick = { isShowDialog = true }
        )

        DrawUserNoPaddingLine(modifier = Modifier.padding(bottom = 16.dp))
    }

    // dialog for selecting and confirming avatar
    if (isShowDialog) {
        AvatarSelectionDialog(
            avatarUrl = avatarUrl,
            fileUri = fileUri,
            isSelected = isSelected,
            onBack = {
                isSelected = false
                isShowDialog = false
            },
            onImageSelected = { uri, uploaded ->
                fileUri = uri
                isSelected = uploaded
            },
            onConfirm = {
                isUploaded = true
                isShowDialog = false
            }
        )
    }
}

/**
 * Displays the user's current avatar with upload indicator when updating.
 */
@Composable
fun AvatarPreviewSection(
    avatarUrl: String,
    isProfileUpdated: Boolean,
    isUploaded: Boolean,
    fileUri: Uri?,
    context: Context,
    scope: CoroutineScope,
    userEntity: UserEntityFirebase,
    userViewModel: UserViewModel,
    firebaseUserViewModel: FirebaseUserViewModel,
    onUploadFinished: () -> Unit
) {
    Box {
        if (avatarUrl.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(brush = BrushPrimaryGradient, blendMode = BlendMode.SrcAtop)
                        }
                    },
                strokeWidth = 4.dp
            )
        } else {
            AsyncImage(
                model = if (isProfileUpdated) fileUri else avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        if (isUploaded && fileUri != null) {
            UploadingOverlay()
            PerformAvatarUpload(
                scope = scope,
                context = context,
                avatarUrl = avatarUrl,
                fileUri = fileUri,
                userEntity = userEntity,
                userViewModel = userViewModel,
                firebaseUserViewModel = firebaseUserViewModel,
                onFinished = onUploadFinished
            )
        }
    }
}

/**
 * Overlay shown when avatar upload is in progress.
 */
@Composable
fun UploadingOverlay() {
    Box(
        modifier = Modifier
//            .matchParentSize()
            .background(Color.Black.copy(alpha = 0.5f))
    )
    CircularProgressIndicator(
        modifier = Modifier
//            .align(Alignment.Center)
            .size(28.dp)
            .graphicsLayer(alpha = 0.99f)
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    drawRect(brush = BrushPrimaryGradient, blendMode = BlendMode.SrcAtop)
                }
            },
        strokeWidth = 2.dp
    )
}

/**
 * Performs avatar image upload to Cloudinary and updates user data in Firebase.
 */
@Composable
fun PerformAvatarUpload(
    scope: CoroutineScope,
    context: Context,
    avatarUrl: String,
    fileUri: Uri,
    userEntity: UserEntityFirebase,
    userViewModel: UserViewModel,
    firebaseUserViewModel: FirebaseUserViewModel,
    onFinished: () -> Unit
) {
    val userEntityFirebase by firebaseUserViewModel.getUserByUid(userEntity.uid).collectAsState(initial = null)

    LaunchedEffect(Unit) {
        scope.launch {
            val publicId = CloudinaryService.getPublicIdFromUrl(avatarUrl)
            val newAvatarUrl = CloudinaryService.updateFile(context, publicId, fileUri)


            if (userEntityFirebase!!.isSuccess) {
                val updatedUserEntityFirebase = userEntityFirebase?.getOrNull()?.copy(avatarUrl = newAvatarUrl!!)

                firebaseUserViewModel.updateUser(updatedUserEntityFirebase!!)
            }


            /*firebaseUserViewModel.updateUser(
                uid = userEntity.uid,
                updatedData = mapOf("avatarUrl" to newAvatarUrl!!)
            ).onSuccess {
                Toast.makeText(context, "Avatar updated successfully.", Toast.LENGTH_LONG).show()
                userViewModel.updateUser(userEntity.copy(avatarUrl = newAvatarUrl))
                onFinished()
            }.onFailure {
                Toast.makeText(context, "Failed to update avatar.", Toast.LENGTH_LONG).show()
            }*/
        }
    }
}

/**
 * Displays the "Change Avatar" button with animation when uploading.
 */
@Composable
fun AvatarChangeButton(
    isUploaded: Boolean,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        if (isUploaded) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(22.dp)
                    .padding(end = 8.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(brush = BrushPrimaryGradient, blendMode = BlendMode.SrcAtop)
                        }
                    },
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                modifier = Modifier
                    .padding(end = 4.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(brush = BrushPrimaryGradient, blendMode = BlendMode.SrcAtop)
                        }
                    }
            )
        }

        Text(
            text = if (isUploaded) "Updating your avatar..." else "Change",
            style = MaterialTheme.typography.titleSmall.copy(color = Color.White)
        )
    }
}

/**
 * Dialog for selecting and confirming a new avatar.
 */
@Composable
fun AvatarSelectionDialog(
    avatarUrl: String,
    fileUri: Uri?,
    isSelected: Boolean,
    onBack: () -> Unit,
    onImageSelected: (Uri?, Boolean) -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
//                .padding(top = 22.dp)
                .background(PrimaryDark),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back Icon",
                    tint = Color.White
                )
            }

            AsyncImage(
                model = if (isSelected && fileUri != null) fileUri else avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier.size(200.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            DrawNoPaddingLine(thickness = 0.5.dp)

            if (isSelected && fileUri != null) {
                TextButton(
                    onClick = onConfirm,
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .background(BrushPrimaryGradient, RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        painter = painterResource(R.drawable.upload_image),
                        contentDescription = "Upload",
                        tint = Color.White,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Confirm and update avatar",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                    )
                }
            } else {
                Text(
                    modifier = Modifier.padding(bottom = 4.dp),
                    text = "Choose your new avatar",
                    style = MaterialTheme.typography.titleMedium.copy(color = GrayTextColor)
                )
            }

            ImageGallery(onSelectedImage = onImageSelected)
        }
    }
}