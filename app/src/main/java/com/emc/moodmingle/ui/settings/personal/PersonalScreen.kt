package com.emc.moodmingle.ui.settings.personal

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.UserEntityFirebase
import com.emc.moodmingle.data.model.UserEntity
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.local.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PersonalScreen(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 26.dp)
            .background(PrimaryDark),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(onBackClick)
        MainContent()
    }
}

@Composable
private fun Header(onBackClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "Back Icon",
                tint = Color.White
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            text = "Personal Information",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun MainContent() {
    val userViewModel = hiltViewModel<UserViewModel>()
    val firebaseUserViewModel = hiltViewModel<FirebaseUserViewModel>()
    val scope = rememberCoroutineScope()

    val loggedUser by userViewModel.getLoggedUserByUid().collectAsState(initial = null)
    val userUid = loggedUser?.uid ?: ""
    val avatarUrl = loggedUser?.avatarUrl ?: ""
    val username = loggedUser?.username ?: ""
    val bio = loggedUser?.bio ?: ""
    val email = loggedUser?.email ?: ""

    val userEntityFirebase by firebaseUserViewModel.getUserByUid(userUid)
        .collectAsState(initial = null)

    fun updateField(value: String, type: String) {
        scope.launch {
            val userEntity = userEntityFirebase!!.getOrNull()

            if (userEntity != null) {
                val updatedUserEntityFirebase = when (type) {
                    "username" -> userEntity.copy(username = value)
                    "bio" -> userEntity.copy(bio = value)
                    "email" -> userEntity.copy(email = value)
                    else -> userEntity
                }

                firebaseUserViewModel.updateUser(updatedUserEntityFirebase)

                val updatedUser = when (type) {
                    "username" -> loggedUser?.copy(username = value)
                    "bio" -> loggedUser?.copy(bio = value)
                    "email" -> loggedUser?.copy(email = value)
                    else -> loggedUser
                }
                userViewModel.updateUser(userEntity = updatedUser!!)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        loggedUser?.let {
            AvatarPicture(avatarUrl, it)
        }

        listOf(
            Triple("Username", R.drawable.username, username),
            Triple("Bio", R.drawable.bio, bio),
            Triple("Email", R.drawable.email, email)
        ).forEach { (title, icon, currentValue) ->
            Information(
                title = title,
                iconRes = icon,
                currentValue = currentValue
            ) { value, type ->
                updateField(value.ifEmpty { currentValue }, type)
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AvatarPicture(avatarUrl: String, userEntity: UserEntity) {
    val firebaseUserViewModel = hiltViewModel<FirebaseUserViewModel>()

    val isUploaded by firebaseUserViewModel.isUploaded.collectAsState()
    val isProfileUpdated by firebaseUserViewModel.isProfileUpdated.collectAsState()

    var isShowDialog by remember { mutableStateOf(false) }
    var isSelected by remember { mutableStateOf(false) }
    var fileUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val userEntityFirebase by firebaseUserViewModel.getUserByUid(userEntity.uid)
        .collectAsState(initial = null)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Avatar Picture")

        Box {
            if (avatarUrl.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawGradient(),
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
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(28.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawGradient(),
                    strokeWidth = 2.dp
                )
            }
        }

        TextButton(onClick = { isShowDialog = true }) {
            if (isUploaded) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(22.dp)
                        .padding(end = 8.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawGradient(),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.edit),
                    contentDescription = "Edit",
                    modifier = Modifier
                        .size(18.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawGradient()
                )

                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = "Change",
                    style = MaterialTheme.typography.titleSmall.copy(color = Color.White)
                )
            }
        }

        DrawNoPaddingLine(modifier = Modifier.padding(bottom = 8.dp), thickness = 0.5.dp)
    }

    if (isShowDialog) {
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
                    .padding(top = 22.dp)
                    .background(PrimaryDark),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        if (isSelected) isSelected = false
                        isShowDialog = false
                    },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back Icon",
                        tint = Color.White
                    )
                }

                AsyncImage(
                    model = if (isSelected) {
                        if (fileUri == null) {
                            avatarUrl
                        } else {
                            fileUri
                        }
                    } else avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                DrawNoPaddingLine(
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 6.dp
                    )
                )

                if (isSelected && fileUri != null) {
                    UploadAvatarButton(
                        fileUri = fileUri,
                        avatarUrl = avatarUrl,
                        loggedUser = userEntity,
                        userEntityFirebase = userEntityFirebase,
                        onShowDialog = { isShowDialog = it }
                    )
                } else {
                    var expanded by remember { mutableStateOf(false) }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(modifier = Modifier.clickable { expanded = !expanded }) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Arrow Down",
                                    modifier = Modifier
                                        .graphicsLayer(alpha = 0.99f)
                                        .drawGradient()
                                )

                                Text(text = "Albums")
                            }


                        }

                        Text(
                            modifier = Modifier.padding(bottom = 8.dp),
                            text = "Choose your new avatar",
                            style = MaterialTheme.typography.titleMedium.copy(color = GrayTextColor)
                        )
                    }
                }

                ImageGallery(
                    onSelectedImage = { uri, uploaded ->
                        fileUri = uri
                        isSelected = uploaded
                    }
                )
            }
        }
    }
}

@Composable
fun UploadAvatarButton(
    fileUri: Uri?,
    avatarUrl: String,
    loggedUser: UserEntity?,
    userEntityFirebase: Result<UserEntityFirebase>?,
    firebaseUserViewModel: FirebaseUserViewModel = hiltViewModel(),
    onShowDialog: (Boolean) -> Unit
) {
    val context = LocalContext.current

    TextButton(
        onClick = {
            fileUri?.let { uri ->
                firebaseUserViewModel.updateAvatar(context, avatarUrl, uri, userEntityFirebase, loggedUser)
                onShowDialog(false)
            }
        },
        modifier = Modifier
            .padding(bottom = 6.dp)
            .background(BrushPrimaryGradient, RoundedCornerShape(8.dp))
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
}

suspend fun loadDeviceImages(context: Context): List<Uri> = withContext(Dispatchers.IO) {
    val imageUris = mutableListOf<Uri>()
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        sortOrder
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val contentUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )
            imageUris.add(contentUri)
        }
    }
    imageUris
}

suspend fun loadDeviceVideos(context: Context): List<Uri?> = withContext(Dispatchers.IO) {
    val uris = mutableListOf<Uri?>()
    val projection = arrayOf(MediaStore.Video.Media._ID)
    val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"
    val query = context.contentResolver.query(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        sortOrder
    )
    query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val contentUri =
                Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id.toString())
            uris.add(contentUri)
        }
    }
    uris
}

suspend fun loadDeviceAudios(context: Context): List<Uri?> = withContext(Dispatchers.IO) {
    val uris = mutableListOf<Uri?>()
    val projection = arrayOf(MediaStore.Audio.Media._ID)
    val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"
    val query = context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        sortOrder
    )
    query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val contentUri =
                Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id.toString())
            uris.add(contentUri)
        }
    }
    uris
}