package com.emc.moodmingle.ui.settings.saved

import android.util.Log
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.data.firebase.model.saved.CollectionEntityFirebase
import com.emc.moodmingle.data.firebase.model.saved.SaveEntityFirebase
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.screens.NewCollectionButton
import com.emc.moodmingle.ui.settings.saved.text.TextCard
import com.emc.moodmingle.ui.settings.saved.utils.EmptyComponent
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.LoadingDialog
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
fun ItemContent(
    userCache: SnapshotStateMap<String, UserEntityFirebase?>,
    postCache: SnapshotStateMap<String, PostEntityFirebase?>,
    save: SaveEntityFirebase,
    contentType: String,
    postViewModelFirebase: PostViewModelFirebase,
    userViewModelFirebase: FirebaseUserViewModel,
    saveViewModelFirebase: SaveViewModelFirebase,
    onRemove: () -> Unit,
) {
    val collectionViewModelFirebase = hiltViewModel<CollectionViewModelFirebase>()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    val userState: State<UserEntityFirebase?> =
        produceState(initialValue = null, save.userUid) {
            value =
                userCache[save.userUid] ?: userViewModelFirebase.getUserByUid(save.userUid)
                    .first()
                    .getOrNull()
                    .also { userCache[save.userUid] = it }
        }

    val postState: State<PostEntityFirebase?> =
        produceState(initialValue = null, save.postId) {
            value = postCache[save.postId] ?: postViewModelFirebase.getPostById(save.postId)
                .first()
                .also { postCache[save.postId] = it }
        }

    val user = userState.value
    val post = postState.value
    val userId = post?.userId ?: ""

    var showSheet by remember { mutableStateOf(false) }
    var type by remember { mutableStateOf("") }
    var selectedCollectionName by remember { mutableStateOf("") }

    when (contentType) {
        "ALL" -> MediaCard(
            user,
            post,
            save,
            onShowSheet = { showSheet = it }
        )

        "TEXT" -> TextCard(
            user,
            post,
            save,
            onShowSheet = { showSheet = it }
        )
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
                        collectionViewModelFirebase.isSaveInCollection(save.id, user.uid)
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
                    onType = {
                        type = it

                        Log.d("ITEM CONTENT", "TYPE: $type")
                    },
                    onShowSheet = { showSheet = it },
                    onLoading = { if (isSavedInCollection) isLoading = it else showDialog = it }
                )

                BottomSheetItem(
                    text = "Remove",
                    iconRes = R.drawable.remove,
                    onType = {
                        type = it
                        onRemove()
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
                "Pin", "Unpin" -> if (type == "Pin") "Pinning" else "Unpinning"
                "Add To Collection", "Remove From Collection $selectedCollectionName" -> {
                    "${if (type == "Add To Collection") "Adding to" else "Removing from"} collection"
                }

                "Remove" -> "Removing"
                else -> ""
            }
        ) {
            scope.launch {
                when (type) {
                    "Pin", "Unpin" -> saveViewModelFirebase.update(saveEntity = save.copy(pinned = !save.pinned))
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
                                collectionViewModelFirebase.getCollectionByNameAndUser(name, userId)

                            collectionViewModelFirebase.update(
                                collection = collection!!.copy(saveIds = collection.saveIds + save.id)
                            )
                        }

                        delay(1000)

                        Toast.makeText(
                            context,
                            "Item ${if (isRemove) "removed" else "added"} to collection $name",
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
                    .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
            }.collectAsState(emptyList())

            val collectionNames = collections.map { it.name }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .background(PrimaryDark)
                    .border(
                        width = 0.5.dp,
                        brush = BrushPrimaryGradient,
                        shape = RectangleShape
                    )
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

                    DrawNoPaddingLine(
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    if (collectionNames.isEmpty()) {
                        EmptyComponent(
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