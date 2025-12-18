package com.emc.moodmingle.ui.post.action.more

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.chat.Conversation
import com.emc.moodmingle.ui.chat.input.sendMessage
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.viewmodel.chat.ConversationViewModel
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun PostChat(postEntity: PostEntityFirebase, onChatClick: (String, String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userViewModelFirebase = hiltViewModel<FirebaseUserViewModel>()
    val conversationViewModel = hiltViewModel<ConversationViewModel>()

    val currentUser by userViewModelFirebase.loggedUser
    val currentUserId = currentUser?.uid
    var avatarUrl by remember { mutableStateOf("") }

    LaunchedEffect(postEntity.userId) {
        avatarUrl =
            userViewModelFirebase.getUserByUid(postEntity.userId).first().getOrNull()?.avatarUrl
                ?: ""
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Talk with the user about this post",
            style = Typography.bodyMedium.copy(
                color = Color.White,
                fontStyle = FontStyle.Italic
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Box(
            modifier = Modifier
                .background(PrimaryDark, CircleShape)
                .clickable {
                    conversationViewModel.checkConversationExists(
                        user1 = currentUserId!!,
                        user2 = postEntity.userId
                    ) { conversation ->
                        scope.launch {
                            if (conversation != null) {
                                sendMessage(
                                    message = "Hey, can we talk about how you’re feeling in your post? I just want to understand what's going on.",
                                    senderId = currentUserId,
                                    receiverId = postEntity.userId,
                                    conversation = conversation,
                                    conversationViewModel = conversationViewModel,
                                    type = "POST",
                                    postId = postEntity.id
                                )

                                Toast.makeText(
                                    context,
                                    "Chat with ${postEntity.username}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                conversationViewModel.createConversation(
                                    Conversation(
                                        creatorId = currentUserId,
                                        pairId = "$currentUserId ${postEntity.userId}",
                                    )
                                )
                            }
                        }
                    }

                    onChatClick(currentUserId, postEntity.userId)
                }
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = avatarUrl,
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