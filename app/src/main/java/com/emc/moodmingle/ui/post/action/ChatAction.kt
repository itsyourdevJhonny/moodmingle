package com.emc.moodmingle.ui.post.action

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.chat.Conversation
import com.emc.moodmingle.ui.chat.input.sendMessage
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.viewmodel.chat.ChatViewModel
import com.emc.moodmingle.viewmodel.chat.ConversationViewModel
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import kotlinx.coroutines.launch

@Composable
fun ChatAction(postEntity: PostEntityFirebase, onChatClick: (String, String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userViewModelFirebase = hiltViewModel<FirebaseUserViewModel>()
    val conversationViewModel = hiltViewModel<ConversationViewModel>()

    val currentUser by userViewModelFirebase.loggedUser
    val currentUserId = currentUser?.uid

    Box(
        modifier = Modifier
            .background(SecondaryDark, CircleShape)
            .size(40.dp)
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

                        Toast.makeText(context, "Chat with ${postEntity.username}", Toast.LENGTH_SHORT).show()
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
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.chat),
            contentDescription = "Share",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}