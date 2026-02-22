package com.emc.moodmingle.ui.create.post.settings.disable

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.settings.PostCommentReactionVisibility
import com.emc.moodmingle.ui.create.post.CreatePostDialogHeader
import com.emc.moodmingle.ui.post.action.toastMessage
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.SwitchButton
import com.emc.moodmingle.utils.components.UserSelector
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel

@Composable
fun DisableCommentReactionDialog(
    commentReactionVisibility: PostCommentReactionVisibility,
    onCommentReactionVisibility: (PostCommentReactionVisibility) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val localCopy = remember(Unit) { commentReactionVisibility }

    var showSelectUserDialog by remember { mutableStateOf(false) }

    Box {
        Scaffold(
            containerColor = Color.Black,
            topBar = {
                CreatePostDialogHeader(
                    label = "Comment/Reaction Visibility",
                    onBack = {
                        if (commentReactionVisibility != localCopy) {
                            toastMessage(context, "Settings Saved")
                        }

                        onDismiss()
                    }
                )
            }
        ) { paddingValues ->
            CommentReactionDialogContent(
                paddingValues,
                commentReactionVisibility,
                onCommentReactionVisibility,
                onShowSelectUserDialog = { showSelectUserDialog = it }
            )
        }

        if (showSelectUserDialog) {
            UserSelector(
                title = "Select People",
                userIds = commentReactionVisibility.selectedUserIds,
                onUsersSelected = { data ->
                    val selectedUserIds = (data as SnapshotStateList<*>).map { it.toString() }
                    onCommentReactionVisibility(
                        commentReactionVisibility.copy(selectedUserIds = selectedUserIds)
                    )
                },
                onDismiss = { showSelectUserDialog = false }
            )
        }
    }
}

@Composable
fun CommentReactionDialogContent(
    paddingValues: PaddingValues,
    commentReactionVisibility: PostCommentReactionVisibility,
    onCommentReactionVisibility: (PostCommentReactionVisibility) -> Unit,
    onShowSelectUserDialog: (Boolean) -> Unit
) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 12.dp)
    ) {
        listOf(
            Triple("Comment", R.drawable.comment, commentReactionVisibility.commentEnabled),
            Triple("Reaction", R.drawable.love, commentReactionVisibility.reactionEnabled)
        ).forEach { (text, icon, enabled) ->
            val isComment = text == "Comment"

            Column(modifier = Modifier.animateContentSize()) {
                Description(enabled, isComment)

                Spacer(Modifier.height(12.dp))

                LabelIndicator(icon, text)

                Column {
                    SwitchButton(
                        enabled,
                        isComment,
                        commentReactionVisibility,
                        onCommentReactionVisibility
                    )

                    if (!enabled) Divider(isComment, startPadding = 28.dp)
                }

                DisableToSelectedPeopleSection(
                    userViewModel,
                    isComment,
                    enabled,
                    commentReactionVisibility,
                    onShowSelectUserDialog
                )

                Divider(isComment)
            }
        }
    }
}

@Composable
private fun Divider(isComment: Boolean, startPadding: Dp = Dp.Unspecified) {
    if (isComment) {
        HorizontalDivider(
            thickness = 0.5.dp,
            modifier = Modifier.padding(bottom = 16.dp, top = 8.dp, start = startPadding)
        )
    }
}

@Composable
private fun SwitchButton(
    enabled: Boolean,
    isComment: Boolean,
    commentReactionVisibility: PostCommentReactionVisibility,
    onCommentReactionVisibility: (PostCommentReactionVisibility) -> Unit,
) {
    Box(modifier = Modifier.padding(start = 16.dp)) {
        SwitchButton(
            label = if (enabled) "Enabled" else "Disabled",
            isChecked = enabled,
            labelColor = GrayTextColor
        ) {
            onCommentReactionVisibility(
                if (isComment) commentReactionVisibility.copy(commentEnabled = it)
                else commentReactionVisibility.copy(reactionEnabled = it)
            )
        }
    }
}

@Composable
private fun LabelIndicator(icon: Int, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = text,
            modifier = Modifier
                .size(20.dp)
                .drawGradient()
        )

        Text(
            text = text,
            fontSize = 18.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.animateContentSize()
        )
    }
}

@Composable
private fun Description(enabled: Boolean, isComment: Boolean) {
    val description = if (isComment) {
        "People ${if (enabled) "will" else "won't"} be able to comment to your post."
    } else {
        "People ${if (enabled) "will" else "won't"} be able to react to your post."
    }

    Text(
        text = description,
        style = Typography.bodyMedium.copy(color = GrayTextColor),
        modifier = Modifier.animateContentSize()
    )
}