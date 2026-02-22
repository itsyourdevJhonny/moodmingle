package com.emc.moodmingle.ui.post.action

import android.content.Context
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.normal.HideEntityFirebase
import com.emc.moodmingle.domain.remote.model.post.normal.PostEntityFirebase
import com.emc.moodmingle.domain.remote.model.favorites.FavoritesEntityFirebase
import com.emc.moodmingle.domain.remote.model.saved.SaveEntityFirebase
import com.emc.moodmingle.di.AppDatabase
import com.emc.moodmingle.ui.post.action.more.InterestAndShareWithOther
import com.emc.moodmingle.ui.post.action.more.PostChat
import com.emc.moodmingle.ui.profile.DrawUserNoPaddingLine
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.remote.HideViewModelFirebase
import com.emc.moodmingle.viewmodel.remote.PostViewModelFirebase
import com.emc.moodmingle.viewmodel.remote.ShareViewModelFirebase
import com.emc.moodmingle.viewmodel.remote.favorites.FavoritesViewModelFirebase
import com.emc.moodmingle.viewmodel.remote.saved.SaveViewModelFirebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreAction(
    postEntity: PostEntityFirebase,
    onShowDialogSheet: (Boolean) -> Unit,
    onChatClick: (String, String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val postViewModel = hiltViewModel<PostViewModelFirebase>()
    val saveViewModel = hiltViewModel<SaveViewModelFirebase>()
    val favoritesViewModel = hiltViewModel<FavoritesViewModelFirebase>()
    val shareViewModel = hiltViewModel<ShareViewModelFirebase>()
    val hideViewModel = hiltViewModel<HideViewModelFirebase>()

    val userDao = remember { AppDatabase.getDatabase(context).userDao() }
    var currentUserUid by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userDao.getLoggedUser()?.uid?.let { currentUserUid = it }
    }

    var saveEntity by remember { mutableStateOf<SaveEntityFirebase?>(null) }
    var hideEntity by remember { mutableStateOf<HideEntityFirebase?>(null) }
    var favoritesEntity by remember { mutableStateOf<FavoritesEntityFirebase?>(null) }

    saveViewModel.getSavedByPostAndUser(
        postId = postEntity.id,
        userUid = currentUserUid,
        callback = {
            saveEntity = it
        }
    )

    hideViewModel.getHiddenByPostAndUser(
        postId = postEntity.id,
        userUid = currentUserUid,
        callback = {
            hideEntity = it
        }
    )

    val shareEntity by remember(postEntity.id, currentUserUid) {
        shareViewModel.getSharedByPostIdAndUserUid(postEntity.id, currentUserUid)
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), 0L)
    }.collectAsState()

    favoritesViewModel.getFavoriteByPostAndUser(
        postId = postEntity.id,
        userUid = currentUserUid,
        callback = {
            favoritesEntity = it
        }
    )

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Icon(
        painter = painterResource(id = R.drawable.more),
        contentDescription = "More",
        tint = Color.White,
        modifier = Modifier
            .size(28.dp)
            .clickable { showSheet = true }
    )

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = Color.Black,
            dragHandle = { BottomSheetDragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DrawNoPaddingLine(modifier = Modifier.padding(bottom = 8.dp))

                if (currentUserUid != postEntity.userId) PostChat(postEntity, onChatClick)

                InterestAndShareWithOther()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryDark, RoundedCornerShape(12.dp))
                ) {
                    CreateMoreAction(
                        iconRes = R.drawable.save_post,
                        title = "${if (saveEntity != null) "Unsave" else "Save"} Post",
                        description = "${if (saveEntity != null) "Unsave" else "Save"} this post for later viewing or sharing."
                    ) {
                        if (saveEntity != null) {
                            saveViewModel.delete(saveEntity!!)
                        } else {
                            saveViewModel.insert(
                                SaveEntityFirebase(
                                    userUid = currentUserUid,
                                    postId = postEntity.id,
                                    time = System.currentTimeMillis()
                                )
                            )
                        }

                        toastMessage(
                            context = context,
                            message = "Post ${if (saveEntity != null) "unsaved" else "saved"} successfully."
                        )

                        showSheet = false
                    }

                    CreateMoreAction(
                        iconRes = R.drawable.add_to_favorite,
                        title = "${if (favoritesEntity != null) "Remove" else "Add"} to Favorites",
                        description = "${if (favoritesEntity != null) "Remove" else "Add"} this post to your favorites list."
                    ) {
                        if (favoritesEntity != null) {
                            favoritesViewModel.delete(favoritesEntity!!)
                        } else {
                            favoritesViewModel.insert(
                                FavoritesEntityFirebase(
                                    userUid = currentUserUid,
                                    postId = postEntity.id,
                                    time = System.currentTimeMillis()
                                )
                            )
                        }

                        toastMessage(
                            context = context,
                            message = "Post ${if (favoritesEntity != null) "removed" else "added"} to your favorites."
                        )

                        showSheet = false
                    }

                    CreateMoreAction(
                        iconRes = R.drawable.share_post,
                        title = "${if (shareEntity != null) "Unshare" else "Share"} Post",
                        description = "${if (shareEntity != null) "Unshare" else "Share"} this post to your profile."
                    ) {
                        showSheet = false
                        onShowDialogSheet(true)
                    }

                    CreateMoreAction(
                        iconRes = R.drawable.hidden,
                        title = "Hide Post",
                        description = "Hide this post to your feed."
                    ) {
                        hideViewModel.insert(
                            HideEntityFirebase(
                                userUid = currentUserUid,
                                postId = postEntity.id,
                                time = System.currentTimeMillis()
                            )
                        )

                        toastMessage(
                            context = context,
                            message = "You won't see this post again."
                        )

                        showSheet = false
                    }

                    CreateMoreAction(
                        iconRes = R.drawable.delete,
                        title = "Delete Post",
                        description = "Delete this post from your profile."
                    ) {
                        showDeleteDialog = true
                    }

                    if (showDeleteDialog) {
                        ConfirmDeleteAction(
                            postViewModel = postViewModel,
                            postEntity = postEntity,
                            context = context,
                            onShowDeleteDialog = { showDeleteDialog = it },
                            onShowSheet = { showSheet = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BottomSheetDragHandle() {
    BottomSheetDefaults.DragHandle(
        modifier = Modifier
            .graphicsLayer(alpha = 0.99f)
            .drawGradient()
    )
}

@Composable
fun ConfirmDeleteAction(
    postViewModel: PostViewModelFirebase,
    postEntity: PostEntityFirebase,
    context: Context,
    onShowDeleteDialog: (Boolean) -> Unit,
    onShowSheet: (Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = {},
        content = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryDark, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.remove),
                        contentDescription = "More",
                        tint = Color.White,
                        modifier = Modifier
                            .graphicsLayer(alpha = 0.99f)
                            .drawGradient()
                    )

                    Text(
                        text = "Confirm delete",
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                        modifier = Modifier.padding(top = 10.dp)
                    )

                    DrawUserNoPaddingLine(modifier = Modifier.padding(vertical = 12.dp))

                    Text(
                        text = "Are you sure you want to delete this post?",
                        style = MaterialTheme.typography.bodySmall.copy(color = GrayTextColor)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                delay(5000)

                                postViewModel.deletePost(postEntity)

                                toastMessage(
                                    context = context,
                                    message = "Post deleted successfully."
                                )

                                onShowDeleteDialog(false)
                                onShowSheet(false)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.remove),
                                contentDescription = "Remove",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = if (isLoading) "Deleting post..." else "Yes",
                            modifier = Modifier.padding(start = 6.dp),
                            color = Color.White
                        )
                    }

                    if (!isLoading) {
                        Button(
                            onClick = { onShowDeleteDialog(false) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = Color.White,
                            )
                            Text(text = "Cancel", color = Color.White)
                        }
                    }
                }
            }
        }
    )
}

fun toastMessage(context: Context, message: String, duration: String = "SHORT") {
    Toast.makeText(
        context,
        message,
        if (duration == "SHORT") Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    ).show()
}

@Composable
fun CreateMoreAction(
    @DrawableRes iconRes: Int,
    title: String,
    description: String,
    onClick: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(true) }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .background(SecondaryDark, CircleShape)
                .padding(8.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = "More",
                tint = Color.White,
                modifier = Modifier
                    .size(20.dp)
                    .drawGradient()
            )
        }

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
            Text(text = title, color = Color.White)
            Text(text = description, fontSize = 12.sp, color = GrayTextColor)
        }
    }
}