package com.emc.moodmingle.ui.settings.saved.media

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.UserEntityFirebase
import com.emc.moodmingle.data.firebase.model.saved.CollectionEntityFirebase
import com.emc.moodmingle.data.firebase.model.saved.SaveEntityFirebase
import com.emc.moodmingle.data.model.post.formatTimeAgo
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.screens.NewCollectionButton
import com.emc.moodmingle.ui.settings.saved.BottomSheetItem
import com.emc.moodmingle.ui.settings.saved.utils.NoResult
import com.emc.moodmingle.ui.theme.BrushGrayGradient
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.LoadingDialog
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.firebase.PostViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.saved.CollectionViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.saved.SaveViewModelFirebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaContent(saves: List<SaveEntityFirebase>, userId: String) {
    val scope = rememberCoroutineScope()
    val postViewModelFirebase = hiltViewModel<PostViewModelFirebase>()
    val userViewModelFirebase = hiltViewModel<FirebaseUserViewModel>()
    val saveViewModelFirebase = hiltViewModel<SaveViewModelFirebase>()
    val collectionViewModelFirebase = hiltViewModel<CollectionViewModelFirebase>()
    val context = LocalContext.current
    var selectedType by remember { mutableStateOf("Image") }

    var loadedData by remember {
        mutableStateOf<List<Triple<PostEntityFirebase, Long, SaveEntityFirebase>>>(
            emptyList()
        )
    }

    // load all posts at once
    LaunchedEffect(saves) {
        val results = mutableListOf<Triple<PostEntityFirebase, Long, SaveEntityFirebase>>()

        saves.forEach { save ->
            val post = postViewModelFirebase.getPostById(save.postId).first()
            if (post?.urls?.isNotEmpty() == true) {
                results.add(Triple(post, save.time, save))
            }
        }

        loadedData = results
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SecondaryDark)
    ) {
        Header { selectedType = it }

        if (loadedData.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.empty),
                    contentDescription = "Empty",
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawGradient()
                )

                Text(
                    text = "You don't have any $selectedType saved post.",
                    color = GrayTextColor,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(loadedData) { (post, time, save) ->
                    var showSheet by remember { mutableStateOf(false) }
                    var type by remember { mutableStateOf("") }
                    var isLoading by remember { mutableStateOf(false) }
                    var showDialog by remember { mutableStateOf(false) }
                    var user by remember { mutableStateOf<UserEntityFirebase?>(null) }
                    var name by remember { mutableStateOf("") }
                    var selectedCollectionName by remember { mutableStateOf("") }

                    LaunchedEffect(post.userId) {
                        user = userViewModelFirebase.getUserByUid(post.userId).first().getOrNull()
                    }

                    if (user != null) {
                        val showSheetCallback = { value: Boolean -> showSheet = value }

                        when (selectedType) {
                            "Image" -> ImageGrid(
                                save,
                                post,
                                user!!,
                                onShowSheet = showSheetCallback
                            )

                            "Video" -> if (post.urls.any(::isVideo)) {
                                VideoGrid(save, post, user!!, onShowSheet = showSheetCallback)
                            }

                            "Audio" -> if (post.urls.any(::isAudio)) {
                                AudioGrid(save, post, user!!, onShowSheet = showSheetCallback)
                            }
                        }

                        if (showSheet) {
                            ModalBottomSheet(
                                onDismissRequest = { showSheet = false },
                                containerColor = PrimaryDark,
                                dragHandle = {
                                    BottomSheetDefaults.DragHandle(
                                        modifier = Modifier
                                            .graphicsLayer(alpha = 0.99f)
                                            .drawGradient()
                                    )
                                }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .height(200.dp)
                                        .fillMaxSize()
                                        .background(PrimaryDark)
                                ) {
                                    DrawNoPaddingLine(
                                        thickness = 0.5.dp,
                                        modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                                    )

                                    BottomSheetItem(
                                        text = if (save.pinned) "Unpin" else "Pin",
                                        iconRes = if (save.pinned) R.drawable.unpin else R.drawable.pin,
                                        onType = { type = it },
                                        onShowSheet = { showSheet = it },
                                        onLoading = { isLoading = it }
                                    )

                                    var isSavedInCollection by remember { mutableStateOf(false) }

                                    LaunchedEffect(save.id, user!!.uid) {
                                        isSavedInCollection =
                                            collectionViewModelFirebase.isSaveInCollection(
                                                saveId = save.id,
                                                userUid = user!!.uid
                                            )
                                    }

                                    BottomSheetItem(
                                        text = if (isSavedInCollection) {
                                            var collectionName by remember { mutableStateOf("") }

                                            LaunchedEffect(userId, save.id) {
                                                val collection =
                                                    collectionViewModelFirebase.getCollectionByUserAndSaved(
                                                        userUid = userId,
                                                        saveId = save.id
                                                    )

                                                collectionName = collection?.name ?: ""
                                                selectedCollectionName = collectionName
                                            }

                                            "Remove From Collection $collectionName"
                                        } else "Add To Collection",
                                        iconRes = R.drawable.collections,
                                        onType = { type = it },
                                        onShowSheet = { showSheet = it },
                                        onLoading = {
                                            if (isSavedInCollection) isLoading = it
                                            else showDialog = it
                                        }
                                    )

                                    BottomSheetItem(
                                        text = "Remove",
                                        iconRes = R.drawable.remove,
                                        onType = {
                                            type = it

                                            scope.launch {
                                                delay(300)
                                                saveViewModelFirebase.delete(save)
                                            }
                                        },
                                        onShowSheet = { showSheet = it },
                                        onLoading = { isLoading = it }
                                    )
                                }
                            }
                        }

                        if (isLoading) {
                            LoadingDialog(
                                text = when (type) {
                                    "Pin" -> "Pinning"
                                    "Add To Collection", "Remove From Collection $selectedCollectionName" -> {
                                        "${if (type == "Add To Collection") "Adding to" else "Removing from"} collection"
                                    }

                                    "Remove" -> "Removing"
                                    else -> ""
                                }
                            ) {
                                scope.launch {
                                    when (type) {
                                        "Pin", "Unpin" -> {
                                            saveViewModelFirebase.update(
                                                saveEntity = save.copy(
                                                    pinned = !save.pinned
                                                )
                                            )
                                        }

                                        "Remove" -> saveViewModelFirebase.delete(save)
                                        "Add To Collection", "Remove From Collection $selectedCollectionName" -> {
                                            val isRemove = type == "Remove From Collection $selectedCollectionName"
                                            var collection: CollectionEntityFirebase?

                                            if (isRemove) {
                                                collection =
                                                    collectionViewModelFirebase.getCollectionByUserAndSaved(
                                                        userUid = userId,
                                                        saveId = save.id
                                                    )

                                                collectionViewModelFirebase.update(
                                                    collection = collection!!.copy(saveIds = collection.saveIds - save.id)
                                                )
                                            } else {
                                                collection =
                                                    collectionViewModelFirebase.getCollectionByNameAndUser(
                                                        name,
                                                        userId
                                                    )

                                                collectionViewModelFirebase.update(
                                                    collection = collection!!.copy(saveIds = collection.saveIds + save.id)
                                                )
                                            }

                                            delay(1000)

                                            Toast.makeText(
                                                context,
                                                "Item ${if (isRemove) "removed" else "added"} to collection (${collection.name})",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    isLoading = false
                                }
                            }
                        }

                        if (showDialog) {
                            Dialog(onDismissRequest = { showDialog = false }) {
                                val collections by remember(userId) {
                                    collectionViewModelFirebase.getCollectionByUser(userId)
                                        .stateIn(
                                            scope,
                                            SharingStarted.WhileSubscribed(5000),
                                            emptyList()
                                        )
                                }.collectAsState(emptyList())

                                val collectionNames = collections.map { it.name }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 480.dp)
                                        .border(
                                            width = 0.5.dp,
                                            brush = BrushPrimaryGradient,
                                            shape = RectangleShape
                                        )
                                        .background(PrimaryDark)
                                        .padding(vertical = 12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            /*.padding(horizontal = 8.dp)*/,
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Select Collection",
                                            style = Typography.titleLarge
                                        )

                                        NewCollectionButton(userId)

                                        DrawNoPaddingLine(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 8.dp))

                                        if (collectionNames.isEmpty()) {
                                            NoResult(
                                                R.drawable.empty,
                                                text = "No collections yet. Create one."
                                            )
                                        } else {
                                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                                items(collectionNames) { collectionName ->
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable {
                                                                isLoading = true
                                                                showDialog = false
                                                                name = collectionName
                                                            }
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(10.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = collectionName,
                                                                overflow = TextOverflow.Ellipsis,
                                                                maxLines = 1,
                                                                modifier = Modifier.widthIn(max = 270.dp)
                                                            )

                                                            Icon(
                                                                imageVector = Icons.Default.Add,
                                                                contentDescription = "Add",
                                                                modifier = Modifier
                                                                    .graphicsLayer(alpha = 0.99f)
                                                                    .drawGradient()
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(onSelectedType: (String) -> Unit) {
    val mediaTypes = listOf(
        "Image" to R.drawable.image,
        "Video" to R.drawable.video,
        "Audio" to R.drawable.audio
    )
    var selected by remember { mutableStateOf("Image") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        mediaTypes.forEach { mediaType ->
            val isSelected = selected == mediaType.first
            TextButton(
                onClick = {
                    selected = mediaType.first
                    onSelectedType(selected)
                },
                modifier = Modifier
                    .width(116.dp)
                    .background(if (isSelected) BrushPrimaryGradient else BrushGrayGradient)
            ) {
                Icon(
                    painter = painterResource(mediaType.second),
                    contentDescription = mediaType.first,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
                Text(
                    text = mediaType.first,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun PostInformation(
    save: SaveEntityFirebase,
    post: PostEntityFirebase,
    user: UserEntityFirebase,
    onShowSheet: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 8.dp)
            .clickable {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Column {
                Text(
                    text = formatTimeAgo(post.timeAgo),
                    style = Typography.labelSmall.copy(color = GrayTextColor)
                )
                Text(
                    text = user.username,
                    fontSize = 13.sp
                )
            }
        }

        if (save.pinned) {
            Box(
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.pin),
                    contentDescription = "Pinned",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Red
                )
            }
        }

        Icon(
            painter = painterResource(R.drawable.more),
            contentDescription = "More",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable { onShowSheet(true) }
        )
    }
}



