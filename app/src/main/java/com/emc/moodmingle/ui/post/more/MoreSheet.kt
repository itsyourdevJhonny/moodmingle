package com.emc.moodmingle.ui.post.more

import android.content.Context
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.normal.NormalPostEntity
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.domain.remote.viewmodel.post.normal.NormalPostViewModel
import com.emc.moodmingle.ui.post.action.BottomSheetDragHandle
import com.emc.moodmingle.ui.post.action.CreateMoreAction
import com.emc.moodmingle.ui.post.action.more.InterestAndShareWithOther
import com.emc.moodmingle.ui.post.action.toastMessage
import com.emc.moodmingle.ui.profile.DrawUserNoPaddingLine
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.chat.checkConversationAndSendMessage
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.remote.chat.ConversationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreSheet(
    currentUserId: String,
    owner: UserEntityFirebase,
    entityId: String,
    onChat: (String, String) -> Unit,
    onShare: () -> Unit,
    onDismissMoreSheet: () -> Unit
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<NormalPostViewModel>()

    val normalPost by remember(entityId) {
        viewModel.getPostById(entityId)
    }.collectAsState(initial = null)

    var openDeleteDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    normalPost?.let { post ->
        val isSaved = post.saverIds.contains(currentUserId)
        val isFavorite = post.favoriterIds.contains(currentUserId)
        val isShared = post.sharerIds.contains(currentUserId)

        ModalBottomSheet(
            onDismissRequest = onDismissMoreSheet,
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
                if (currentUserId != owner.uid) Chat(currentUserId, owner, entityId, onChat)

                InterestAndShareWithOther()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryDark, RoundedCornerShape(12.dp))
                ) {
                    getActionsInformation(isSaved, isFavorite, isShared)
                        .forEach { (iconRes, title, description) ->
                            if (iconRes == R.drawable.delete && currentUserId != owner.uid) return@forEach

                            ActionIem(
                                context,
                                iconRes,
                                title,
                                description,
                                post,
                                currentUserId,
                                isSaved,
                                isFavorite,
                                viewModel,
                                onShare,
                                onOpenDeleteDialog = { openDeleteDialog = it },
                                onDismissMoreSheet
                            )
                        }
                }
            }
        }

        if (openDeleteDialog) {
            ConfirmDeleteDialog(
                viewModel,
                post,
                context,
                onDismissMoreSheet,
                onDismissDeleteDialog = { openDeleteDialog = it },
            )
        }
    }
}

@Composable
private fun ActionIem(
    context: Context,
    iconRes: Int,
    title: String,
    description: String,
    post: NormalPostEntity,
    currentUserId: String,
    isSaved: Boolean,
    isFavorite: Boolean,
    viewModel: NormalPostViewModel,
    onShare: () -> Unit,
    onOpenDeleteDialog: (Boolean) -> Unit,
    onDismissMoreSheet: () -> Unit
) {
    CreateMoreAction(iconRes, title, description) {
        when (iconRes) {
            R.drawable.save_post -> viewModel.updatePost(postEntity = post.copy(saverIds = if (isSaved) post.saverIds - currentUserId else post.saverIds + currentUserId))
            R.drawable.add_to_favorite -> viewModel.updatePost(postEntity = post.copy(favoriterIds = if (isFavorite) post.favoriterIds - currentUserId else post.favoriterIds + currentUserId))
            R.drawable.share_post -> onShare()
            R.drawable.hidden -> viewModel.updatePost(postEntity = post.copy(hiderIds = post.hiderIds + currentUserId))
            R.drawable.delete -> onOpenDeleteDialog(true)
        }

        val message = when (iconRes) {
            R.drawable.save_post -> "Post ${if (isSaved) "unsaved" else "saved"} successfully."
            R.drawable.add_to_favorite -> "Post ${if (isFavorite) "removed" else "added"} to your favorites."
            R.drawable.hidden -> "You won't see this post again."
            else -> ""
        }

        if (iconRes != R.drawable.share_post && iconRes != R.drawable.delete) {
            toastMessage(context, message)
            onDismissMoreSheet()
        }
    }
}

@Composable
private fun ConfirmDeleteDialog(
    viewModel: NormalPostViewModel,
    post: NormalPostEntity,
    context: Context,
    onDismissMoreSheet: () -> Unit,
    onDismissDeleteDialog: (Boolean) -> Unit,
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
                                delay(3000)
                                viewModel.deletePost(post)
                                toastMessage(context, message = "Post deleted successfully.")
                                onDismissDeleteDialog(false)
                                onDismissMoreSheet()
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
                            text = if (isLoading) "Deleting post..." else "Delete",
                            modifier = Modifier.padding(start = 6.dp),
                            color = Color.White
                        )
                    }

                    if (!isLoading) {
                        Button(
                            onClick = { onDismissDeleteDialog(false) },
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

@Composable
private fun Chat(
    currentUserId: String,
    owner: UserEntityFirebase,
    entityId: String,
    onChat: (String, String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val conversationViewModel = hiltViewModel<ConversationViewModel>()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Talk with the user about this post",
            style = Typography.bodyMedium.copy(color = Color.White, fontStyle = FontStyle.Italic),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Box(
            modifier = Modifier
                .background(PrimaryDark, CircleShape)
                .clickable {
                    checkConversationAndSendMessage(
                        senderId = currentUserId,
                        receiverId = owner.uid,
                        entityId = entityId,
                        type = "NORMAL_POST",
                        scope = scope,
                        conversationViewModel = conversationViewModel
                    )

                    onChat(currentUserId, owner.uid)
                }
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = owner.avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )

                Column {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Arrow Back",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Arrow Forward",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(BrushPrimaryGradient, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.chat),
                        contentDescription = "Screenshot",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

private fun getActionsInformation(
    isSaved: Boolean,
    isFavorite: Boolean,
    isShared: Boolean
): List<Triple<Int, String, String>> = listOf(
    Triple(
        R.drawable.save_post,
        "${if (isSaved) "Unsave" else "Save"} Post",
        "${if (isSaved) "Unsave" else "Save"} this post for later viewing or sharing."
    ),
    Triple(
        R.drawable.add_to_favorite,
        "${if (isFavorite) "Remove" else "Add"} to Favorites",
        "${if (isFavorite) "Remove" else "Add"} this post to your favorites list."
    ),
    Triple(
        R.drawable.share_post,
        "${if (isShared) "Unshare" else "Share"} Post",
        "${if (isShared) "Unshare" else "Share"} this post to your profile."
    ),
    Triple(
        R.drawable.hidden,
        "Hide Post",
        "Hide this post to your feed."
    ),
    Triple(
        R.drawable.delete,
        "Delete Post",
        "Delete this post from your profile."
    )
)