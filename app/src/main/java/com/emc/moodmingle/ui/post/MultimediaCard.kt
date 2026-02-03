package com.emc.moodmingle.ui.post

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.data.model.post.formatTimeAgo
import com.emc.moodmingle.di.AppDatabase
import com.emc.moodmingle.ui.post.action.ChatAction
import com.emc.moodmingle.ui.post.action.CommentAction
import com.emc.moodmingle.ui.post.action.DrawLine
import com.emc.moodmingle.ui.post.action.MoreAction
import com.emc.moodmingle.ui.post.action.PostInformation
import com.emc.moodmingle.ui.post.action.PostInteractions
import com.emc.moodmingle.ui.post.action.ReactionAction
import com.emc.moodmingle.ui.post.action.ShareAction
import com.emc.moodmingle.ui.post.action.ShareButton
import com.emc.moodmingle.ui.post.action.ShareTitle
import com.emc.moodmingle.ui.post.action.executeShareOperation
import com.emc.moodmingle.ui.post.action.formatText
import com.emc.moodmingle.ui.post.text.ExpandableAutoDetectClickableText
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.LoadingDialog
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.firebase.CommentViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.firebase.ReactionViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.ShareViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.notification.NotificationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultimediaCard(
    isCLickable: Boolean = true,
    composable: @Composable () -> Unit,
    postEntity: PostEntityFirebase,
    userEntity: UserEntityFirebase?,
    postType: String,
    onClick: (String) -> Unit,
    currentUserUid: String = "",
    showShareSheet: Boolean,
    onShowShareSheet: (Boolean) -> Unit,
    onChatClick: (String, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isViewPost by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .background(PrimaryDark)
            .clickable { if (isCLickable) isViewPost = true },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PostUserInformation(postEntity, userEntity, onClick)
            PostMoodAndEmoji(
                postEntity,
                onShowDialogSheet = onShowShareSheet,
                onChatClick
            )
        }

        PostHashtag(postEntity)

        PostCaption(postEntity)

        if (postType != "TEXT") {
            PostDescription(postEntity)
        }

        composable()

        PostActions(postEntity, currentUserUid, onShowShareSheet, onChatClick)
    }

    if (showShareSheet) {
        ShareSheet(onShowShareSheet, sheetState, postEntity, userEntity)
    }

    if (isViewPost) {
        ViewPost(
            postEntity,
            userEntity,
            onClick,
            showShareSheet,
            onShowShareSheet,
            onChatClick,
            onDismiss = { isViewPost = false }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ShareSheet(
    onShowShareSheet: (Boolean) -> Unit,
    sheetState: SheetState,
    postEntity: PostEntityFirebase,
    userEntity: UserEntityFirebase?
) {
    ModalBottomSheet(
        onDismissRequest = { onShowShareSheet(false) },
        sheetState = sheetState,
        containerColor = Color.Black,
        dragHandle = null,
    ) {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val commentViewModelFirebase = hiltViewModel<CommentViewModelFirebase>()
        val shareViewModelFirebase = hiltViewModel<ShareViewModelFirebase>()
        val notificationViewModel = hiltViewModel<NotificationViewModel>()
        val reactionViewModelFirebase = hiltViewModel<ReactionViewModelFirebase>()

        val userDao = remember { AppDatabase.getDatabase(context).userDao() }
        var currentUserId by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            userDao.getLoggedUser()?.uid?.let { currentUserId = it }
        }

        val shareEntity by remember(postEntity.id, currentUserId) {
            shareViewModelFirebase.getSharedByPostIdAndUserUid(postEntity.id, currentUserId)
        }.collectAsState(initial = null)

        val totalReactions by remember(postEntity.id) {
            reactionViewModelFirebase.getReactionsCountByPostId(postEntity.id)
        }.collectAsState(initial = 0L)

        val totalComments by remember(postEntity.id) {
            commentViewModelFirebase.getCommentCountByPostId(postEntity.id)
        }.collectAsState(initial = 0L)

        val totalShares by remember(postEntity.id) {
            shareViewModelFirebase.getShareCountByPostId(postEntity.id)
        }.collectAsState(initial = 0L)

        Column(
            modifier = Modifier
                .background(Color.Black)
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ShareTitle(shareEntity != null)

            DrawLine()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryDark, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = "Post Information",
                        style = MaterialTheme.typography.titleSmall
                    )

                    PostInformation(
                        text = formatText(postEntity.hashtag.replace("#", ""), 23),
                        iconRes = R.drawable.hashtag,
                        style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.SansSerif)
                    )

                    PostInformation(
                        text = "\"" + formatText(postEntity.caption, 35) + "\"",
                        iconRes = R.drawable.caption,
                        style = MaterialTheme.typography.titleSmall.copy(fontStyle = FontStyle.Italic)
                    )

                    PostInformation(
                        text = formatText(postEntity.description, 39),
                        iconRes = R.drawable.description,
                        style = MaterialTheme.typography.bodySmall
                    )

                    PostInteractions(userEntity, totalReactions, totalComments, totalShares)
                }
            }

            ShareButton(
                onShowSheet = onShowShareSheet,
                shareEntity = shareEntity,
                shareViewModel = shareViewModelFirebase,
                postId = postEntity.id,
                username = userEntity?.username,
                onLoading = { isLoading = it },
                isLoading = isLoading
            )

            val isShared = shareEntity != null

            if (isLoading) {
                LoadingDialog(if (isShared) "Unsharing post..." else "Sharing post...") {
                    scope.launch {
                        delay(2000)

                        onShowShareSheet(false)
                        isLoading = false

                        executeShareOperation(
                            isShared = isShared,
                            shareEntity = shareEntity,
                            shareViewModel = shareViewModelFirebase,
                            userUid = currentUserId,
                            postId = postEntity.id,
                            username = userEntity?.username,
                            context = context,
                            scope = scope,
                            notificationViewModel = notificationViewModel,
                            postUserId = postEntity.userId
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostUserInformation(
    postEntity: PostEntityFirebase,
    userEntity: UserEntityFirebase?,
    onClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(top = 6.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarImage(
            avatarUrl = userEntity?.avatarUrl ?: "",
            onClick = { onClick(postEntity.userId) },
            userUid = postEntity.userId
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                modifier = Modifier
                    .widthIn(max = 220.dp)
                    .clickable { onClick(postEntity.userId) },
                text = postEntity.username,
                style = Typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = formatTimeAgo(postEntity.timeAgo),
                fontSize = 11.sp,
                color = GrayTextColor
            )
        }
    }
}

fun formatUsername(username: String): String {
    return if (username.length > 15) username.substring(0, 15) + "..." else username
}

@Composable
fun PostMoodAndEmoji(
    postEntity: PostEntityFirebase,
    onShowDialogSheet: (Boolean) -> Unit,
    onChatClick: (String, String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        MoreAction(postEntity, onShowDialogSheet, onChatClick)

        Surface(color = Color(0xFFF0E6FF), shape = CircleShape) {
            Text(
                text = postEntity.moodEmoji + "" + postEntity.mood,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .background(BrushPrimaryGradient)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                color = Color.White
            )
        }
    }
}

@Composable
fun PostCaption(postEntity: PostEntityFirebase) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InformationIcon(R.drawable.caption, "Caption")

        ExpandableAutoDetectClickableText(
            fullText = "\"" + postEntity.caption + "\"",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White,
                fontStyle = FontStyle.Italic
            ),
            hasPadding = false
        )
    }
}

@Composable
fun PostHashtag(postEntity: PostEntityFirebase) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InformationIcon(R.drawable.hashtag, "Hashtag")

        ExpandableAutoDetectClickableText(
            fullText = postEntity.hashtag.replace("#", ""),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            ),
            hasPadding = false
        )
    }
}

@Composable
fun PostDescription(postEntity: PostEntityFirebase) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InformationIcon(
            R.drawable.description,
            "Description",
            modifier = Modifier.align(Alignment.Top)
        )

        ExpandableAutoDetectClickableText(
            fullText = postEntity.description,
            style = MaterialTheme.typography.bodyMedium,
            hasPadding = false,
        )
    }
}

@Composable
fun InformationIcon(@DrawableRes iconRes: Int, label: String, modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(iconRes),
        contentDescription = label,
        tint = Color.White,
        modifier = modifier
            .size(24.dp)
            .graphicsLayer(alpha = 0.99f)
            .drawGradient()
    )
}

@Composable
fun PostActions(
    postEntity: PostEntityFirebase,
    currentUserUid: String,
    onShowShareSheet: (Boolean) -> Unit,
    onChatClick: (String, String) -> Unit
) {
    val commentViewModel = hiltViewModel<CommentViewModelFirebase>()
    val shareViewModel = hiltViewModel<ShareViewModelFirebase>()
    val reactionViewModel = hiltViewModel<ReactionViewModelFirebase>()
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val currentUser by userViewModel.loggedUser

    val comments by remember(postEntity.id) {
        commentViewModel.getCommentCountByPostId(postId = postEntity.id)
    }.collectAsState(initial = 0)

    val reactions by remember(postEntity.id) {
        reactionViewModel.getReactionsCountByPostId(postId = postEntity.id)
    }.collectAsState(initial = 0)

    val shares by remember(postEntity.id) {
        shareViewModel.getShareCountByPostId(postId = postEntity.id)
    }.collectAsState(initial = 0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionContainer(
            action = { ReactionAction(postEntity, reactionViewModel, currentUserUid) },
            count = reactions
        )

        ActionContainer(
            action = { CommentAction(postEntity, onChatClick) },
            count = comments
        )

        ActionContainer(
            action = {
                ShareAction(
                    onShowShareSheet,
                    boxModifier = Modifier.background(SecondaryDark, CircleShape)
                )
            },
            count = shares,
        )

        if (currentUser?.uid != postEntity.userId) ChatAction(postEntity, onChatClick)
    }
}

@Composable
fun ActionContainer(action: @Composable () -> Unit, count: Long) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        action()

        if (count > 0) {
            Text(text = "$count", style = Typography.bodyMedium.copy(color = GrayTextColor))
        }
    }
}

@Composable
fun AvatarImage(avatarUrl: String, onClick: (String) -> Unit, userUid: String) {
    val model = remember(avatarUrl) { avatarUrl }

    AsyncImage(
        model = model,
        contentDescription = "Avatar",
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.3f))
            .clickable { onClick(userUid) },
        contentScale = ContentScale.Crop
    )
}
