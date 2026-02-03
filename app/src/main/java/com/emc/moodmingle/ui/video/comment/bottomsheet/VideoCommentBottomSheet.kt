package com.emc.moodmingle.ui.video.comment.bottomsheet

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.video.VideoComment
import com.emc.moodmingle.data.firebase.viewmodel.video.VideoCommentViewModel
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.video.comment.icons.VideoCommentCloseIcon
import com.emc.moodmingle.ui.video.comment.icons.VideoCommentDislikeIcon
import com.emc.moodmingle.ui.video.comment.icons.VideoCommentHeartIcon
import com.emc.moodmingle.ui.video.comment.icons.VideoCommentSendOrReplyIcon
import com.emc.moodmingle.ui.video.comment.icons.VideoCommentTriggerAndSupportIcon
import com.emc.moodmingle.ui.video.comment.information.VideoCommentInformation
import com.emc.moodmingle.ui.video.comment.information.VideoCommenterAvatar
import com.emc.moodmingle.ui.video.comment.input.SendCommentOrReplyOrEdit
import com.emc.moodmingle.ui.video.comment.input.VideoCommentEdit
import com.emc.moodmingle.ui.video.comment.input.VideoCommentReplying
import com.emc.moodmingle.ui.video.comment.input.VideoCommentTextField
import com.emc.moodmingle.ui.video.comment.input.emotion.VideoCommentDisplaySelectedEmotion
import com.emc.moodmingle.ui.video.comment.input.emotion.VideoCommentEmotionUpload
import com.emc.moodmingle.ui.video.comment.media.VideoCommentDisplaySelectedMedia
import com.emc.moodmingle.ui.video.comment.media.VideoCommentMedia
import com.emc.moodmingle.ui.video.comment.media.VideoCommentUploadMedia
import com.emc.moodmingle.ui.video.comment.more.VideoCommentMoreAction
import com.emc.moodmingle.ui.video.comment.reply.VideoCommentReplies
import com.emc.moodmingle.ui.video.comment.reply.VideoCommentReply
import com.emc.moodmingle.utils.SwitchButton
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.modifier.grayCircleBorder
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoCommentBottomSheet(
    onDismiss: () -> Unit,
    videoUrl: String,
    onActiveRepliesComment: (VideoComment?) -> Unit,
    onUserClick: (String) -> Unit,
    onChatClick: (String, String) -> Unit,
    onRemix: (String, String) -> Unit
) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val videoCommentViewModel = hiltViewModel<VideoCommentViewModel>()

    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }

    var sheetOffset by remember { mutableFloatStateOf(0f) }
    val maxOffset = 600f

    val currentUser by userViewModel.loggedUser
    val currentUserId = currentUser?.uid ?: ""

    val commentsCount by remember(videoUrl) {
        videoCommentViewModel.getCommentCountByVideoUrl(videoUrl)
    }.collectAsState(initial = 0)

    var replyText by remember { mutableStateOf("") }
    var commentText by remember { mutableStateOf("") }
    var replyEnabled by remember { mutableStateOf(false) }
    var selectedComment by remember { mutableStateOf<VideoComment?>(null) }

    var editEnabled by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryDark)
            .offset { IntOffset(0, sheetOffset.roundToInt()) }
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    sheetOffset = (sheetOffset + delta).coerceAtLeast(0f)
                },
                onDragStopped = { velocity ->
                    if (sheetOffset > maxOffset) {
                        onDismiss()
                    } else {
                        sheetOffset = 0f
                    }
                }
            )
    ) {
        Scaffold(
            topBar = {
                VideoCommentSheetDragHandle(commentsCount, videoUrl, onDismiss)
            },
            bottomBar = {
                InputField(
                    commentText,
                    videoCommentViewModel,
                    videoUrl,
                    currentUserId,
                    listState,
                    replyEnabled,
                    replyText,
                    selectedComment,
                    editEnabled,
                    focusRequester,
                    onCommentTextChange = { commentText = it },
                    onReplyText = { replyText = it },
                    onReplyEnabled = { replyEnabled = it },
                    onSelectedComment = { selectedComment = it },
                    onEditEnabled = { editEnabled = it }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                DisplayVideoComments(
                    currentUserId,
                    videoUrl,
                    replyText,
                    selectedComment,
                    replyEnabled,
                    onReplyText = { replyText = it },
                    onReplyEnabled = { replyEnabled = it },
                    onSelectedComment = { selectedComment = it },
                    onCommentTextChange = { commentText = it },
                    onEditEnabled = { editEnabled = it },
                    userViewModel,
                    videoCommentViewModel,
                    listState,
                    focusRequester,
                    onActiveRepliesComment,
                    onUserClick,
                    onChatClick,
                    onRemix
                )
            }
        }
    }
}

@Composable
private fun DisplayVideoComments(
    currentUserId: String,
    videoUrl: String,
    replyText: String,
    selectedComment: VideoComment?,
    replyEnabled: Boolean,
    onReplyText: (String) -> Unit,
    onReplyEnabled: (Boolean) -> Unit,
    onSelectedComment: (VideoComment?) -> Unit,
    onCommentTextChange: (String) -> Unit,
    onEditEnabled: (Boolean) -> Unit,
    userViewModel: FirebaseUserViewModel,
    videoCommentViewModel: VideoCommentViewModel,
    listState: LazyListState,
    focusRequester: FocusRequester,
    onActiveRepliesComment: (VideoComment?) -> Unit,
    onUserClick: (String) -> Unit,
    onChatClick: (String, String) -> Unit,
    onRemix: (String, String) -> Unit
) {
    val comments by remember(videoUrl) {
        videoCommentViewModel.getCommentsByVideoUrl(videoUrl)
    }.collectAsState(initial = emptyList())

    if (comments.isNotEmpty()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(Modifier.height(1.dp)) }

            items(comments) { comment ->
                val commenterResult by remember(comment.commenterId) {
                    userViewModel.getUserByUid(comment.commenterId)
                }.collectAsState(initial = null)

                val commenter = commenterResult?.getOrNull()
                val isSelected = selectedComment?.id == comment.id && replyEnabled

                var showMoreAction by remember { mutableStateOf(false) }

                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .animateItem()
                        .pointerInput(Unit) {
                            detectTapGestures(onLongPress = { showMoreAction = true })
                        }
                ) {
                    VideoCommenterAvatar(commenter, onUserClick)

                    Column(modifier = Modifier.fillMaxWidth()) {
                        VideoCommentInformation(
                            commenter,
                            comment,
                            onShowMoreAction = { showMoreAction = it },
                            onUserClick
                        )
                        VideoCommentMedia(comment)
                        VideoCommentReplies(
                            comment,
                            onShowReplies = { onActiveRepliesComment(comment) })
                        VideoCommentReply(commenter, replyText, isSelected)

                        Row(
                            modifier = Modifier
                                .align(Alignment.End)
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            VideoCommentTriggerAndSupportIcon(comment)

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                VideoCommentHeartIcon(comment, currentUserId, isSelected)
                                VideoCommentDislikeIcon(comment, currentUserId, isSelected)
                                VideoCommentSendOrReplyIcon(
                                    currentUserId,
                                    comment,
                                    replyText,
                                    isSelected,
                                    focusRequester,
                                    onReplyText,
                                    onReplyEnabled,
                                    onSelectedComment,
                                    onCommentTextChange
                                )
                                VideoCommentCloseIcon(isSelected, onReplyEnabled, onSelectedComment)
                            }
                        }

                        HorizontalDivider(
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                if (showMoreAction) {
                    VideoCommentMoreAction(
                        currentUserId,
                        commenter,
                        comment,
                        onSelectedComment,
                        onReplyEnabled,
                        onEditEnabled,
                        onChatClick,
                        onDismiss = { showMoreAction = false },
                        onRemix
                    )
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.drawGradient())
        }
    }
}

@Composable
private fun InputField(
    commentText: String,
    videoCommentViewModel: VideoCommentViewModel,
    videoUrl: String,
    currentUserId: String,
    listState: LazyListState,
    replyEnabled: Boolean,
    replyText: String,
    selectedComment: VideoComment?,
    editEnabled: Boolean,
    focusRequester: FocusRequester,
    onCommentTextChange: (String) -> Unit,
    onReplyText: (String) -> Unit,
    onReplyEnabled: (Boolean) -> Unit,
    onSelectedComment: (VideoComment?) -> Unit,
    onEditEnabled: (Boolean) -> Unit,
) {
    var mediaUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isSelected by remember { mutableStateOf(false) }
    var isAnonymous by remember { mutableStateOf(false) }
    var emotion by remember { mutableStateOf("" to "") }
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryDark)
    ) {
        DrawNoPaddingLine(thickness = 0.5.dp)

        VideoCommentReplying(replyEnabled, editEnabled)
        VideoCommentEdit(editEnabled, selectedComment, onEditEnabled, onSelectedComment)

        AnimatedVisibility(
            visible = isFocused && !replyEnabled && !editEnabled,
            enter = slideInHorizontally(
                initialOffsetX = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
        ) {
            Column {
                HideIcon()
                AnonymousSwitchButton(isAnonymous, onCheckedChange = { isAnonymous = it })
                VideoCommentDisplaySelectedEmotion(emotion, onSelectedEmotion = { emotion = it })

                VideoCommentDisplaySelectedMedia(
                    isSelected,
                    mediaUris,
                    onSelectedUris = { mediaUris = it },
                    onSelected = { isSelected = it }
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VideoCommentUploadMedia(
                        mediaUris,
                        isSelected,
                        onSelectedUris = { mediaUris = it },
                        onSelected = { isSelected = it }
                    )

                    VideoCommentEmotionUpload(emotion.second, onSelectedEmotion = { emotion = it })
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            VideoCommentTextField(
                commentText,
                onCommentTextChange,
                replyEnabled,
                editEnabled,
                onReplyText,
                focusRequester,
                onFocusedChange = { isFocused = it }
            )

            SendCommentOrReplyOrEdit(
                replyEnabled,
                editEnabled,
                replyText,
                videoCommentViewModel,
                selectedComment,
                currentUserId,
                onSelectedComment,
                onReplyEnabled,
                onEditEnabled,
                onReplyText,
                commentText,
                videoUrl,
                mediaUris,
                listState,
                emotion.second,
                isAnonymous,
                onCommentTextChange,
                onSelectedUris = { mediaUris = it },
                onSelectedEmotion = { emotion = it },
                onCheckedChange = { isAnonymous = it }
            )
        }
    }
}

@Composable
private fun ColumnScope.HideIcon() {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .padding(top = 8.dp)
            .align(Alignment.CenterHorizontally)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(SecondaryDark, CircleShape)
                .grayCircleBorder()
                .clickable { focusManager.clearFocus() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.visibility_off),
                contentDescription = "Close",
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun AnonymousSwitchButton(isAnonymous: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SwitchButton(
            label = "Comment Anonymously",
            isChecked = isAnonymous,
            onCheckedChange = onCheckedChange
        )
    }

    HorizontalDivider(
        thickness = 0.5.dp,
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
    )
}