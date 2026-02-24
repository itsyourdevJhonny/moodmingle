package com.emc.moodmingle.ui.chat.conversation

import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.domain.remote.model.chat.Conversation
import com.emc.moodmingle.ui.screens.getOtherUserId
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.dialogFullSizeProperties
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.remote.chat.ConversationViewModel
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Composable
fun UsersRowList(
    conversations: List<Conversation>,
    currentUserId: String,
    onChatClick: (String, String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val userViewModelFirebase = hiltViewModel<FirebaseUserViewModel>()

    var showAddDialog by remember { mutableStateOf(false) }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item { AddIcon(onShowAddDialog = { showAddDialog = it }) }

        items(conversations) { conversation ->
            val userResult by remember(conversation.pairId) {
                userViewModelFirebase.getUserByUid(
                    getOtherUserId(conversation.pairId, currentUserId)
                ).stateIn(scope, SharingStarted.WhileSubscribed(5000), null)
            }.collectAsState(initial = null)

            val receiver = userResult?.getOrNull()

            Box(modifier = Modifier.clickable { onChatClick(currentUserId, receiver?.uid ?: "") }) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ReceiverAvatar(receiver?.avatarUrl ?: "")
                    ReceiverUsername(receiver?.username)
                }
            }
        }
    }

    if (showAddDialog) {
        AddConversationDialog(currentUserId, onChatClick, onDismiss = { showAddDialog = false })
    }
}

@Composable
private fun AddIcon(onShowAddDialog: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp)
            .background(BrushPrimaryGradient, CircleShape)
            .size(50.dp)
            .clickable { onShowAddDialog(true) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            modifier = Modifier.size(32.dp),
            tint = Color.White
        )
    }
}

@Composable
private fun ReceiverAvatar(avatarUrl: String) {
    AsyncImage(
        model = avatarUrl,
        contentDescription = "Avatar",
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(
                width = 0.5.dp,
                brush = BrushPrimaryGradient,
                shape = CircleShape
            ),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun ReceiverUsername(username: String?) {
    Text(
        text = username ?: "",
        style = Typography.bodySmall.copy(color = Color.White, fontWeight = FontWeight.Bold),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.width(42.dp)
    )
}

@Composable
private fun AddConversationDialog(
    currentUserId: String,
    onChatClick: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchedUsers by remember { mutableStateOf(emptyList<UserEntityFirebase>()) }

    Dialog(
        onDismissRequest = {},
        properties = dialogFullSizeProperties()
    ) {
        Column(
            modifier = Modifier
                .padding(top = 38.dp)
                .fillMaxSize()
                .background(PrimaryDark)
        ) {
            Header(onDismiss)
            SearchField(onSearchUsers = { searchedUsers = it })
            Content(currentUserId, searchedUsers, onChatClick)
        }
    }
}

@Composable
private fun Header(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, bottom = 16.dp)
            .fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.clickable { onDismiss() },
            tint = Color.White
        )

        Text(
            text = "Find Someone",
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun SearchField(onSearchUsers: (List<UserEntityFirebase>) -> Unit) {
    val userViewModelFirebase = hiltViewModel<FirebaseUserViewModel>()
    val allUsers by userViewModelFirebase.getAllUsers().collectAsState(initial = emptyList())

    var value by remember { mutableStateOf("") }

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        value = value,
        onValueChange = {
            value = it

            val searchedUsers = allUsers.filter { user ->
                it.isNotBlank() && user.username.lowercase().contains(it.lowercase())
            }

            onSearchUsers(searchedUsers)
        },
        placeholder = { Text(text = "Search user...") },
        suffix = {
            if (value.isNotBlank()) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear",
                    tint = Color.White,
                    modifier = Modifier.clickable { value = "" }
                )
            }
        },
        shape = CircleShape,
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = SecondaryDark,
            focusedPlaceholderColor = GrayTextColor,
            focusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color.White
        )
    )
}

@Composable
private fun Content(
    currentUserId: String,
    searchedUsers: List<UserEntityFirebase>,
    onChatClick: (String, String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val conversationViewModel = hiltViewModel<ConversationViewModel>()

    if (searchedUsers.isNotEmpty()) {
        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(searchedUsers) { user ->
                if (currentUserId == user.uid) return@items

                Box(
                    modifier = Modifier.clickable {
                        startChattingUser(
                            currentUserId,
                            user,
                            context,
                            scope,
                            conversationViewModel,
                            onChatClick
                        )
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        SearchedUserInformation(user)
                        ChatIcon()
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchedUserInformation(user: UserEntityFirebase) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Text(
            text = user.username,
            style = Typography.bodyMedium.copy(color = Color.White),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 260.dp)
        )
    }
}

@Composable
private fun ChatIcon() {
    Icon(
        painter = painterResource(R.drawable.chat),
        contentDescription = "Chat",
        modifier = Modifier
            .size(22.dp)
            .graphicsLayer(alpha = 0.99f)
            .drawGradient()
    )
}

private fun startChattingUser(
    currentUserId: String,
    user: UserEntityFirebase,
    context: Context,
    scope: CoroutineScope,
    conversationViewModel: ConversationViewModel,
    onChatClick: (String, String) -> Unit
) {
    conversationViewModel.checkConversationExists(
        user1 = currentUserId,
        user2 = user.uid
    ) { conversation ->
        scope.launch {
            if (conversation == null) {
                conversationViewModel.createConversation(
                    Conversation(
                        creatorId = currentUserId,
                        pairId = "$currentUserId ${user.uid}",
                        lastMessage = "Start chatting with ${user.username}",
                        lastMessageTime = System.currentTimeMillis()
                    )
                )

                Toast.makeText(
                    context,
                    "Chat with ${user.username}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    onChatClick(currentUserId, user.uid)
}