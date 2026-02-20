package com.emc.moodmingle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.domain.remote.model.chat.Conversation
import com.emc.moodmingle.ui.chat.conversation.UsersRowList
import com.emc.moodmingle.ui.chat.utils.ChatTimerFormatter
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.remote.chat.ConversationViewModel
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@Composable
fun ConversationScreen(onBack: () -> Unit, onChatClick: (String, String) -> Unit) {
    val userViewModelFirebase = hiltViewModel<FirebaseUserViewModel>()
    val conversationViewModel = hiltViewModel<ConversationViewModel>()
    val currentUser by userViewModelFirebase.loggedUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, bottom = 42.dp)
            .background(PrimaryDark),
        content = {
            Header(onBack)
            Content(currentUser, conversationViewModel, onChatClick)
        }
    )
}

@Composable
private fun Header(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.clickable { onBack() },
            tint = Color.White
        )

        Text(
            text = "Conversations",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                textAlign = TextAlign.Center
            )
        )

        Icon(
            painter = painterResource(R.drawable.chat),
            contentDescription = "Chat",
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawGradient()
        )
    }
}

@Composable
private fun Content(
    currentUser: UserEntityFirebase?,
    conversationViewModel: ConversationViewModel,
    onChatClick: (String, String) -> Unit
) {
    val currentUserId = currentUser?.uid ?: ""
    val scope = rememberCoroutineScope()

    val conversations by remember(currentUserId) {
        conversationViewModel.getConversationsByUser(currentUserId)
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())
    }.collectAsState(initial = emptyList())

    UsersRowList(conversations, currentUserId, onChatClick)

    DisplayConversations(conversations, currentUserId, scope, onChatClick)
}

@Composable
private fun DisplayConversations(
    conversations: List<Conversation>,
    currentUserId: String,
    scope: CoroutineScope,
    onChatClick: (String, String) -> Unit
) {
    val userViewModelFirebase = hiltViewModel<FirebaseUserViewModel>()

    LazyColumn {
        items(conversations) { conversation ->
            val userResult by remember(conversation.pairId) {
                userViewModelFirebase.getUserByUid(
                    getOtherUserId(conversation.pairId, currentUserId)
                ).stateIn(scope, SharingStarted.WhileSubscribed(5000), null)
            }.collectAsState(initial = null)

            val receiver = userResult?.getOrNull()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onChatClick(currentUserId, receiver?.uid ?: "") }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = receiver?.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .border(
                                width = 0.5.dp,
                                brush = BrushPrimaryGradient,
                                shape = CircleShape
                            ),
                        contentScale = ContentScale.Crop
                    )

                    Column {
                        Text(
                            text = receiver?.username ?: "",
                            style = Typography.bodyLarge.copy(color = Color.White, fontWeight = FontWeight.Black),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val prefix = if (currentUserId == conversation.lastMessageUserId) "You" else receiver?.username ?: ""

                            Text(
                                text = "$prefix: ${conversation.lastMessage}",
                                style = Typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.width(180.dp)
                            )

                            Text(
                                text = ChatTimerFormatter.formatChatTimeAgo(conversation.lastMessageTime),
                                style = Typography.bodySmall.copy(color = GrayTextColor)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getOtherUserId(pairId: String, currentUserId: String): String {
    val ids = pairId.split(" ")
    return ids.firstOrNull { it != currentUserId } ?: ""
}