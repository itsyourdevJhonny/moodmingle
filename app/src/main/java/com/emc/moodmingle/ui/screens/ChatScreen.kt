package com.emc.moodmingle.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.UserEntityFirebase
import com.emc.moodmingle.data.firebase.model.chat.ChatMessage
import com.emc.moodmingle.data.firebase.model.chat.Conversation
import com.emc.moodmingle.ui.chat.DraggableSuggestions
import com.emc.moodmingle.ui.chat.PostMessageContent
import com.emc.moodmingle.ui.chat.SayHiContent
import com.emc.moodmingle.ui.chat.TextMessageContent
import com.emc.moodmingle.ui.chat.DeletedMessageContent
import com.emc.moodmingle.ui.chat.EditMessage
import com.emc.moodmingle.ui.chat.EditedMessageContent
import com.emc.moodmingle.ui.chat.action.MessageSideActions
import com.emc.moodmingle.ui.chat.action.ShowActionsBottomSheet
import com.emc.moodmingle.ui.chat.input.ChatTextField
import com.emc.moodmingle.ui.chat.input.sendMessage
import com.emc.moodmingle.ui.chat.reply.PostMessageRepliedContent
import com.emc.moodmingle.ui.chat.reply.PostReply
import com.emc.moodmingle.ui.chat.reply.TextMessageRepliedContent
import com.emc.moodmingle.ui.chat.reply.TextReply
import com.emc.moodmingle.ui.chat.settings.ChatSettingsDialog
import com.emc.moodmingle.ui.chat.utils.AnimatedReplyContainer
import com.emc.moodmingle.ui.chat.utils.ChatTimerFormatter
import com.emc.moodmingle.ui.chat.utils.MessageSuggestion
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.BrushSecondaryTertiaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.TertiaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.TimerFormatter
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.chat.ConversationViewModel
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.firebase.PostViewModelFirebase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(senderId: String, receiverId: String, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val state = rememberScrollState()

    val userViewModelFirebase = hiltViewModel<FirebaseUserViewModel>()
    val conversationViewModel = hiltViewModel<ConversationViewModel>()

    var isPostReplyEnabled by remember { mutableStateOf(false) }
    var isTextReplyEnabled by remember { mutableStateOf(false) }
    var isMessageEdited by remember { mutableStateOf(false) }

    var postEntity by remember { mutableStateOf<PostEntityFirebase?>(null) }
    var chatMessage by remember { mutableStateOf<ChatMessage?>(null) }
    var messageToEdit by remember { mutableStateOf<ChatMessage?>(null) }

    var message by remember { mutableStateOf("") }

    val pageSize = 20

    val userResult by remember(receiverId) {
        userViewModelFirebase.getUserByUid(receiverId)
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), null)
    }.collectAsState(initial = null)

    val sender = userResult?.getOrNull()

    LaunchedEffect(Unit) {
        conversationViewModel.getConversation(senderId, receiverId)
    }

    val conversation by conversationViewModel.conversation.collectAsState()
    val messages = conversation?.messages ?: emptyList()

    var loadedCount by remember { mutableIntStateOf(minOf(pageSize, messages.size)) }

    LaunchedEffect(messages.size) {
        loadedCount = minOf(messages.size, loadedCount)
    }

    var isLoadingOlder by remember { mutableStateOf(false) }

    var prevScrollMax by remember { mutableIntStateOf(0) }
    var prevScrollValue by remember { mutableIntStateOf(0) }

    val autoScrollThresholdPx = with(LocalDensity.current) { 200.dp.toPx() }

    val startIndex = maxOf(0, messages.size - loadedCount)
    val visibleMessages = messages.subList(startIndex, messages.size)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp, bottom = 42.dp)
            .background(PrimaryDark)
    ) {
        Header(conversation, conversationViewModel, sender, onBack)

        if (conversation == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.BottomCenter))
            }
        } else {
            if (messages.isEmpty()) {
                SayHiContent(
                    senderId,
                    receiverId,
                    conversation!!,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(state)
                            .padding(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        conversation?.let { ReceiverInformation(sender, it) }

                        var lastMessageDate: String? = null
                        visibleMessages.forEach { msg ->
                            val label = ChatTimerFormatter.dateLabelFor(msg.timestamp)
                            if (lastMessageDate == null || lastMessageDate != label) {
                                DateDivider(label)
                                lastMessageDate = label
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            ) {
                                MessageBubble(
                                    conversation,
                                    msg,
                                    senderId,
                                    sender?.avatarUrl.orEmpty(),
                                    isPostReplyEnabled,
                                    isTextReplyEnabled,
                                    onPostReplyEnabled = { isPostReplyEnabled = it },
                                    onTextReplyEnabled = { isTextReplyEnabled = it },
                                    onPostMessageReplied = { isReplied, post ->
                                        isPostReplyEnabled = isReplied
                                        postEntity = post
                                    },
                                    onTextMessageReplied = { isReplied, chat ->
                                        isTextReplyEnabled = isReplied
                                        chatMessage = chat
                                    },
                                    onEditMessage = { isEdited, editMessage ->
                                        isMessageEdited = isEdited
                                        messageToEdit = editMessage

                                        Log.d("CHAT SCREEN", "CHAT MESSAGE TO EDIT: $messageToEdit")
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    LaunchedEffect(state.value) {
                        if (state.value <= 40 && !isLoadingOlder && loadedCount < messages.size) {
                            isLoadingOlder = true
                            prevScrollMax = state.maxValue
                            prevScrollValue = state.value

                            loadedCount = minOf(messages.size, loadedCount + pageSize)
                        }
                    }

                    LaunchedEffect(loadedCount) {
                        if (isLoadingOlder) {
                            snapshotFlow { state.maxValue }
                                .collect { newMax ->
                                    val delta = newMax - prevScrollMax
                                    state.scrollTo(prevScrollValue + delta)
                                    isLoadingOlder = false
                                    return@collect
                                }
                        }
                    }

                    LaunchedEffect(messages.size) {
                        snapshotFlow { state.maxValue }
                            .collect { max ->
                                val distanceFromBottom = max - state.value

                                if (distanceFromBottom <= autoScrollThresholdPx) {
                                    withFrameNanos { }
                                    state.animateScrollTo(state.maxValue)
                                }
                                return@collect
                            }
                    }
                }
            }
        }

        BottomTextField(
            conversation = conversation,
            message = message,
            onValueChange = { message = it },
            senderId = senderId,
            receiverId = receiverId,
            conversationViewModel = conversationViewModel,
            isPostReplyEnabled = isPostReplyEnabled,
            onPostMessageReplied = { isPostReplyEnabled = it },
            postEntity = postEntity,
            isTextReplyEnabled = isTextReplyEnabled,
            onTextMessageReplied = { isTextReplyEnabled = it },
            chatMessage = chatMessage,
            isMessageEdited = isMessageEdited,
            onMessageEdited = { isMessageEdited = it },
            messageToEdit = messageToEdit
        )
    }
}

@Composable
private fun DateDivider(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            style = Typography.bodyMedium.copy(brush = BrushPrimaryGradient),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ReceiverInformation(sender: UserEntityFirebase?, conversation: Conversation) {
    Column(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .shadow(
                ambientColor = TertiaryDark,
                spotColor = PurpleDark,
                elevation = 24.dp,
                shape = RoundedCornerShape(bottomStart = 38.dp, bottomEnd = 38.dp),
                clip = false
            )
            .background(
                color = SecondaryDark,
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 38.dp,
                    bottomEnd = 38.dp
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = sender?.avatarUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .padding(top = 8.dp)
                .border(
                    width = 0.5.dp,
                    brush = BrushPrimaryGradient,
                    shape = CircleShape
                )
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Text(
            text = sender?.username ?: "",
            color = Color.White,
            fontWeight = FontWeight.W900,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Box(
            modifier = Modifier.background(BrushPrimaryGradient, CircleShape),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.view),
                    contentDescription = "View",
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )

                Text(
                    text = "View Profile",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = "Created: ${TimerFormatter.formatFullDateTime(conversation.createdTime)}",
            style = Typography.bodySmall.copy(color = GrayTextColor),
            modifier = Modifier.padding(bottom = 8.dp)
        )

//        DrawNoPaddingLine(thickness = 0.5.dp)
    }
}

@Composable
fun BottomTextField(
    conversation: Conversation?,
    message: String,
    onValueChange: (String) -> Unit,
    senderId: String,
    receiverId: String,
    conversationViewModel: ConversationViewModel,
    isPostReplyEnabled: Boolean,
    onPostMessageReplied: (Boolean) -> Unit,
    postEntity: PostEntityFirebase?,
    isTextReplyEnabled: Boolean,
    onTextMessageReplied: (Boolean) -> Unit,
    chatMessage: ChatMessage?,
    messageToEdit: ChatMessage?,
    isMessageEdited: Boolean,
    onMessageEdited: (Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val textSuggestions = MessageSuggestion.textSuggestions
    val emojiSuggestions = MessageSuggestion.emojiSuggestions

    Column {
        AnimatedReplyContainer(visible = !isPostReplyEnabled && !isTextReplyEnabled && !isMessageEdited) {
            conversation?.let {
                DraggableSuggestions(
                    emojiSuggestions,
                    textSuggestions,
                    senderId,
                    receiverId,
                    conversation,
                    conversationViewModel,
                    message,
                )
            }
        }

        AnimatedReplyContainer(visible = isPostReplyEnabled) {
            postEntity?.let {
                PostReply(postEntity, onPostMessageReplied)
            }
        }

        AnimatedReplyContainer(visible = isTextReplyEnabled) {
            chatMessage?.let {
                TextReply(chatMessage, onTextMessageReplied)
            }
        }

        AnimatedReplyContainer(visible = isMessageEdited) {
            messageToEdit?.let {
                EditMessage(onMessageEdited, messageToEdit)
            }
        }

        Row(
            modifier = Modifier
                .background(PrimaryDark)
                .drawBehind {
                    val strokeWidth = 0.3.dp.toPx()
                    drawLine(
                        brush = BrushPrimaryGradient,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = strokeWidth
                    )
                }
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChatTextField(message, onValueChange, Modifier.weight(1f))

            IconButton(
                onClick = {
                    if (message.isNotBlank()) {
                        onValueChange("")
                        scope.launch {
                            if (isPostReplyEnabled) {
                                onPostMessageReplied(false)

                                postEntity?.let {
                                    sendMessage(
                                        message,
                                        senderId,
                                        receiverId,
                                        conversation,
                                        conversationViewModel,
                                        type = "POST_REPLIED",
                                        postId = it.id
                                    )
                                }
                            } else if (isTextReplyEnabled) {
                                onTextMessageReplied(false)

                                chatMessage?.let {
                                    sendMessage(
                                        message,
                                        senderId,
                                        receiverId,
                                        conversation,
                                        conversationViewModel,
                                        type = "TEXT_REPLIED",
                                        replyMessage = chatMessage.message
                                    )
                                }
                            } else if (isMessageEdited) {
                                onMessageEdited(false)

                                messageToEdit?.let {
                                    conversation?.let {
                                        val updatedMessage = messageToEdit.copy(type = "EDITED", message = message)

                                        val newMessages = conversation.messages.map { msg ->
                                            if (msg.timestamp == updatedMessage.timestamp) updatedMessage else msg
                                        }

                                        val updatedConversation = conversation.copy(
                                            lastMessage = "Message edited",
                                            lastMessageTime = System.currentTimeMillis(),
                                            messages = newMessages
                                        )

                                        conversationViewModel.updateConversation(updatedConversation)
                                    }
                                }
                            } else {
                                sendMessage(
                                    message,
                                    senderId,
                                    receiverId,
                                    conversation,
                                    conversationViewModel
                                )
                            }
                        }
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.send),
                    contentDescription = "Send",
                    modifier = Modifier
                        .graphicsLayer(alpha = 0.99f)
                        .drawGradient()
                )
            }
        }
    }
}

@Composable
private fun Header(
    conversation: Conversation?,
    conversationViewModel: ConversationViewModel,
    sender: UserEntityFirebase?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSettingsDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.clickable { onBack() },
                tint = Color.White
            )

            Text(
                text = sender?.username ?: "",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W900
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 265.dp)
            )
        }

        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Chat",
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawGradient()
                .clickable { showSettingsDialog = true }
        )
    }

    if (showSettingsDialog) {
        ChatSettingsDialog(
            conversation,
            conversationViewModel,
            onDismiss = { showSettingsDialog = false },
            onBack
        )
    }
}

@Composable
fun MessageBubble(
    conversation: Conversation?,
    chatMessage: ChatMessage,
    senderId: String,
    avatarUrl: String,
    isPostReplyEnabled: Boolean,
    isTextReplyEnabled: Boolean,
    onPostReplyEnabled: (Boolean) -> Unit,
    onTextReplyEnabled: (Boolean) -> Unit,
    onPostMessageReplied: (Boolean, PostEntityFirebase) -> Unit,
    onTextMessageReplied: (Boolean, ChatMessage) -> Unit,
    onEditMessage: (Boolean, ChatMessage) -> Unit
) {
    val isOwn = chatMessage.senderId == senderId
    val postViewModelFirebase = hiltViewModel<PostViewModelFirebase>()
    val post by postViewModelFirebase.getPostById(chatMessage.postId).collectAsState(initial = null)

    var isShowActions by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!isOwn) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .border(
                            width = 0.5.dp,
                            brush = BrushPrimaryGradient,
                            shape = CircleShape
                        )
                        .size(32.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                var isDoubleTapped by remember { mutableStateOf(false) }

                Row {
                    if (isOwn && chatMessage.type != "DELETED") {
                        MessageSideActions(
                            chatMessage,
                            post,
                            isPostReplyEnabled,
                            isTextReplyEnabled,
                            onPostReplyEnabled,
                            onTextReplyEnabled,
                            onPostMessageReplied,
                            onTextMessageReplied,
                            Modifier.align(Alignment.Bottom),
                            iconRes = R.drawable.reply_left,
                            xOffset = -8
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                brush = if (!isOwn) BrushSecondaryTertiaryGradient else BrushPrimaryGradient,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(0.dp)
                            .widthIn(max = 250.dp)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = { isDoubleTapped = !isDoubleTapped },
                                    onLongPress = {
                                        if (chatMessage.type != "DELETED" && chatMessage.type != "POST" && isOwn) {
                                            isShowActions = !isShowActions
                                        }
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when (chatMessage.type) {
                            "TEXT" -> TextMessageContent(chatMessage)
                            "TEXT_REPLIED" -> TextMessageRepliedContent(chatMessage, isOwn)
                            "POST" -> PostMessageContent(chatMessage, isOwn)
                            "POST_REPLIED" -> PostMessageRepliedContent(chatMessage, isOwn)
                            "DELETED" -> DeletedMessageContent()
                            "EDITED" -> EditedMessageContent(chatMessage)
                        }

                        if (isDoubleTapped) {
                            Icon(
                                painter = painterResource(R.drawable.love),
                                contentDescription = "Love",
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(20.dp)
                                    .offset(x = 16.dp, y = 16.dp),
                                tint = Color.Red
                            )
                        }
                    }

                    if (!isOwn && chatMessage.type != "DELETED") {
                        MessageSideActions(
                            chatMessage,
                            post,
                            isPostReplyEnabled,
                            isTextReplyEnabled,
                            onPostReplyEnabled,
                            onTextReplyEnabled,
                            onPostMessageReplied,
                            onTextMessageReplied,
                            Modifier.align(Alignment.Bottom),
                            iconRes = R.drawable.reply_left,
                            xOffset = 8
                        )
                    }
                }

                Text(
                    text = TimerFormatter.formatTimestampToAmPm(chatMessage.timestamp),
                    style = Typography.bodySmall.copy(color = TertiaryDark),
                    modifier = Modifier.align(if (isOwn) Alignment.End else Alignment.Start)
                )
            }
        }
    }

    if (isShowActions) {
        ShowActionsBottomSheet(
            conversation,
            chatMessage,
            onDismiss = { isShowActions = false },
            onEditMessage
        )
    }
}

