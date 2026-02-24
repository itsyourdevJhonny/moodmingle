package com.emc.moodmingle.ui.chat.action

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.chat.ChatMessage
import com.emc.moodmingle.domain.remote.model.chat.Conversation
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.viewmodel.remote.chat.ConversationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowActionsBottomSheet(
    conversation: Conversation?,
    chatMessage: ChatMessage,
    onDismiss: () -> Unit,
    onEditMessage: (Boolean, ChatMessage) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = null,
        containerColor = SecondaryDark
    ) {
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            CreateAction(
                type = "EDITED",
                iconRes = R.drawable.edit,
                label = "Edit this message",
                conversation = conversation,
                chatMessage = chatMessage,
                onDismiss = onDismiss,
                onEditMessage = onEditMessage
            )

            CreateAction(
                type = "DELETED",
                iconRes = R.drawable.remove,
                label = "Delete this message",
                conversation = conversation,
                chatMessage = chatMessage,
                onDismiss = onDismiss,
                onEditMessage = onEditMessage
            )
        }
    }
}

@Composable
private fun CreateAction(
    type: String,
    @DrawableRes iconRes: Int,
    label: String,
    conversation: Conversation?,
    chatMessage: ChatMessage,
    onDismiss: () -> Unit,
    onEditMessage: (Boolean, ChatMessage) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val conversationViewModel = hiltViewModel<ConversationViewModel>()

    Box(
        modifier = Modifier
            .clickable {
                if (type == "DELETED") {
                    scope.launch {
                        conversation?.let {
                            val updatedMessage = chatMessage.copy(type = type)

                            val newMessages = conversation.messages.map { msg ->
                                if (msg.timestamp == updatedMessage.timestamp) updatedMessage else msg
                            }

                            val updatedConversation = conversation.copy(
                                lastMessage = "Message deleted",
                                lastMessageTime = System.currentTimeMillis(),
                                messages = newMessages
                            )

                            conversationViewModel.updateConversation(
                                updatedConversation
                            )
                        }
                    }

                    Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show()
                } else {
                    onEditMessage(true, chatMessage)
                }

                onDismiss()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                modifier = Modifier.size(20.dp),
                tint = if (type == "DELETED") Color.Red else Color.White
            )

            Text(text = label, color = Color.White)
        }
    }
}