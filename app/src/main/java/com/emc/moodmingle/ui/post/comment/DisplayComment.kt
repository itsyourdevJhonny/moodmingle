package com.emc.moodmingle.ui.post.comment

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.CommentEntityFirebase
import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.chat.Conversation
import com.emc.moodmingle.data.firebase.model.notification.NotificationEntity
import com.emc.moodmingle.data.model.UserEntity
import com.emc.moodmingle.data.model.post.formatTimeAgo
import com.emc.moodmingle.di.AppDatabase
import com.emc.moodmingle.ui.profile.DrawUserNoPaddingLine
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.chat.ConversationViewModel
import com.emc.moodmingle.viewmodel.firebase.CommentViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.firebase.notification.NotificationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayComment(postEntity: PostEntityFirebase, onChatClick: (String, String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val commentViewModel = hiltViewModel<CommentViewModelFirebase>()
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val notificationViewModel = hiltViewModel<NotificationViewModel>()

    val currentUserDao = remember { AppDatabase.getDatabase(context).userDao() }
    var currentUserEntity by remember { mutableStateOf<UserEntity?>(null) }

    val currentUser by userViewModel.loggedUser
    val currentUserId = currentUser?.uid ?: ""
    val postUserId = postEntity.userId
    val postId = postEntity.id

    val comments by commentViewModel.getCommentsByPostId(postId)
        .collectAsState(initial = emptyList())

    val allUsers by userViewModel.getAllUsers().collectAsState(initial = emptyList())
    val userLookup = remember(allUsers, comments) { allUsers.associateBy { it.uid } }

    var newCommentText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        currentUserEntity = currentUserDao.getLoggedUser()
    }

    Scaffold(
        containerColor = Color.Black,
        bottomBar = {
            CommentInputBar(
                newCommentText = newCommentText,
                onTextChange = { newCommentText = it },
                onSendClick = {
                    if (newCommentText.isNotBlank()) {
                        val commentEntityFirebase = CommentEntityFirebase(
                            userUid = currentUserId,
                            postId = postId,
                            message = newCommentText
                        )

                        scope.launch {
                            commentViewModel.createComment(commentEntityFirebase)
                            newCommentText = ""

                            Toast.makeText(context, "Posting comment...", Toast.LENGTH_SHORT).show()

                            listState.animateScrollToItem(0)

                            Toast.makeText(context, "Comment Posted", Toast.LENGTH_SHORT).show()

                            val userNotification =
                                notificationViewModel.getNotificationByEntityId(postId)

                            if (userNotification == null) {
                                val newNotification = NotificationEntity(
                                    userId = postUserId,
                                    entityId = postId,
                                    users = listOf(currentUserId),
                                    type = "COMMENT"
                                )

                                Log.d("DisplayComment", "Notification created: $newNotification")

                                notificationViewModel.createNotification(newNotification)
                            } else {
                                val isExists = userNotification.users.contains(currentUserId)

                                if (isExists) {
                                    notificationViewModel.updateNotification(
                                        userNotification.copy(timestamp = System.currentTimeMillis())
                                    )
                                } else {
                                    notificationViewModel.updateNotification(
                                        userNotification.copy(
                                            users = userNotification.users + postUserId,
                                            timestamp = System.currentTimeMillis()
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .background(PrimaryDark),
            state = listState
        ) {
            items(comments, key = { it.id }) { comment ->
                val user = userLookup[comment.userUid]

                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = user?.avatarUrl ?: "",
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .border(
                                width = 0.1.dp,
                                brush = BrushPrimaryGradient,
                                shape = CircleShape
                            )
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = user?.username ?: "",
                                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 254.dp)
                            )

                            RemoveIcon(comment, commentViewModel)
                        }

                        Text(
                            text = comment.message,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        val timestamp = comment.time
                        val milliseconds =
                            timestamp.seconds * 1000 + timestamp.nanoseconds / 1_000_000

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = formatTimeAgo(milliseconds),
                                color = Color.Gray,
                                fontSize = 12.sp
                            )

                            if (currentUserId != comment.userUid) {
                                ChatIcon(
                                    currentUserId,
                                    postUserId,
                                    postEntity.username,
                                    onChatClick
                                )
                            }
                        }

                        DrawUserNoPaddingLine(Modifier.padding(top = 6.dp), thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun RemoveIcon(comment: CommentEntityFirebase, commentViewModel: CommentViewModelFirebase) {
    Icon(
        painter = painterResource(R.drawable.remove),
        contentDescription = "Remove",
        modifier = Modifier
            .size(18.dp)
            .clickable { commentViewModel.deleteComment(comment) },
        tint = Color.Red
    )
}

@Composable
private fun ChatIcon(
    currentUserId: String,
    postUserId: String,
    username: String,
    onChatClick: (String, String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val conversationViewModel = hiltViewModel<ConversationViewModel>()

    Icon(
        painter = painterResource(R.drawable.chat),
        contentDescription = "Chat",
        modifier = Modifier
            .size(20.dp)
            .graphicsLayer(alpha = 0.99f)
            .drawGradient()
            .clickable {
                conversationViewModel.checkConversationExists(
                    user1 = currentUserId,
                    user2 = postUserId
                ) { conversation ->
                    scope.launch {
                        if (conversation != null) {
                            Toast.makeText(context, "Chat with $username", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            conversationViewModel.createConversation(
                                Conversation(
                                    creatorId = currentUserId,
                                    pairId = "$currentUserId $postUserId",
                                )
                            )
                        }
                    }
                }

                onChatClick(currentUserId, postUserId)
            }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentInputBar(
    newCommentText: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TextField(
            value = newCommentText,
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 56.dp, max = 150.dp)
                .background(BrushPrimaryGradient, RoundedCornerShape(8.dp)),
            placeholder = { Text("Add a comment...") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                unfocusedLabelColor = GrayTextColor
            ),
            maxLines = 5
        )

        IconButton(onClick = onSendClick) {
            Icon(
                painter = painterResource(id = R.drawable.send),
                contentDescription = "Send",
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )
        }
    }
}