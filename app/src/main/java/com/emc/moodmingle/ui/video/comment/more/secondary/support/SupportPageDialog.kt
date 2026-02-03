package com.emc.moodmingle.ui.video.comment.more.secondary.support

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.data.firebase.model.video.VideoComment
import com.emc.moodmingle.data.model.post.formatTimeAgo
import com.emc.moodmingle.ui.post.text.ExpandableAutoDetectClickableText
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.TertiaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.utils.modifier.roundedGrayBorder
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import kotlin.random.Random

private data class SupportMessageGroup(
    val type: SupportType,
    val messages: List<String>
)

private enum class SupportType {
    ENCOURAGEMENT,
    EMPATHY,
    VALIDATION,
    CHECK_IN,
    SHARE_RESOURCES
}

@Composable
fun SupportPageDialog(currentUserId: String, comment: VideoComment, onDismiss: () -> Unit) {
    var selectedType by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryDark),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SupportPageHeader(currentUserId, selectedType, message, comment, onDismiss)
            SupportPageContent(
                comment,
                selectedType,
                message,
                onSelectedType = { selectedType = it },
                onMessageChanged = { message = it }
            )
        }
    }
}

@Composable
fun SupportPageContent(
    comment: VideoComment,
    selectedType: String,
    message: String,
    onSelectedType: (String) -> Unit,
    onMessageChanged: (String) -> Unit
) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val commenter by remember(comment.commenterId) {
        userViewModel.getUserById(comment.commenterId)
    }.collectAsState(initial = null)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TitleAndSubtitleAndDescription()

        Column {
            Line()
            CommentContent(commenter, comment)
            Line()
            SupportOptions(selectedType, message, onSelectedType, onMessageChanged)
        }
    }
}

@Composable
private fun Line() {
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp))
}

@Composable
private fun SupportOptions(
    selectedType: String,
    message: String,
    onSelectedType: (String) -> Unit,
    onMessageChanged: (String) -> Unit
) {
    var isSelected by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "How would you like to show support?",
            style = Typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        if (isSelected) {
            Column {
                SelectedOption(selectedType, onSelected = { isSelected = it })
                CustomSupportInputField(selectedType, message, onMessageChanged)
            }
        } else {
            SelectOption(onSelectedType, onSelected = { isSelected = it })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomSupportInputField(
    selectedType: String,
    message: String,
    onMessageChanged: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    var showInputSheet by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }

    val selectedSupportType = runCatching {
        SupportType.valueOf(selectedType.uppercase().replace(" ", "_").replace("-", "_"))
    }.getOrNull()

    val supportMessages = getSupportSuggestionMessages()

    val messages = supportMessages
        .firstOrNull { it.type == selectedSupportType }
        ?.messages
        ?: emptyList()

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
//            .padding(horizontal = 8.dp)
    ) {
        Line()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(
                text = "Your Message",
                style = Typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = " ($selectedType)",
                style = Typography.bodyMedium.copy(color = GrayTextColor)
            )
        }

        TextField(
            value = message,
            onValueChange = {},
            placeholder = {
                Text(
                    text = "Write a kind and supportive message...",
                    style = Typography.bodyMedium
                )
            },
            readOnly = true,
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = SecondaryDark,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                focusedContainerColor = SecondaryDark,
                focusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 150.dp)
                .padding(horizontal = 8.dp)
                .roundedGrayBorder(8.dp)
                .animateContentSize()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionIcon(
                    iconRes = R.drawable.suggestion_text,
                    text = "Suggestions",
                    onClick = { showSuggestions = true }
                )

                ActionIcon(
                    iconRes = R.drawable.random,
                    text = "Random",
                    onClick = { onMessageChanged(messages[Random.nextInt(messages.size)]) }
                )
            }

            ActionIcon(
                iconRes = R.drawable.edit,
                text = "${if (message.isEmpty()) "Write" else "Edit"} Message",
                onClick = { showInputSheet = true; focusRequester.requestFocus() }
            )
        }

        Text(
            text = "Support messages should be kind and non-judgemental.\n" +
                    "Please avoid using profanity or offensive language.",
            style = Typography.bodySmall.copy(color = GrayTextColor, fontStyle = FontStyle.Italic),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }

    if (showInputSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showInputSheet = false
                focusManager.clearFocus()
            },
            dragHandle = { BottomSheetDefaults.DragHandle(modifier = Modifier.drawGradient()) },
            containerColor = PrimaryDark,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            Box(modifier = Modifier.heightIn(min = 200.dp)) {
                TextField(
                    value = message,
                    onValueChange = onMessageChanged,
                    placeholder = {
                        Text(
                            text = "Write a kind and supportive message...",
                            style = Typography.bodyMedium
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = SecondaryDark,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        focusedContainerColor = TertiaryDark,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(150.dp)
                        .roundedGrayBorder(8.dp)
                        .focusRequester(focusRequester)
                )
            }
        }
    }

    if (showSuggestions) {
        SupportMessageSuggestionsDialog(
            selectedType,
            messages,
            onDismiss = { showSuggestions = false },
            onMessageSelected = onMessageChanged
        )
    }
}

@Composable
fun SupportMessageSuggestionsDialog(
    selectedType: String,
    messages: List<String>,
    onDismiss: () -> Unit,
    onMessageSelected: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(color = PrimaryDark, shape = RoundedCornerShape(16.dp))
                .roundedGrayBorder(16.dp)
                .padding(16.dp)
        ) {
            Text(
                text = "Suggested Messages",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = selectedType,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(messages) { message ->
                    SupportMessageItem(
                        message = message,
                        onClick = {
                            onMessageSelected(message)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SupportMessageItem(message: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color = Color.Green.copy(alpha = 0.15f))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyMedium.copy(color = Color.White))
    }
}

@Composable
private fun ActionIcon(@DrawableRes iconRes: Int, text: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = text,
            modifier = Modifier
                .size(16.dp)
                .drawGradient()
        )

        Text(text = text, style = Typography.bodyMedium)
    }
}

@Composable
private fun SelectedOption(selectedType: String, onSelected: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = true,
                onClick = {},
                colors = RadioButtonDefaults.colors(
                    selectedColor = PurplePrimary,
                    unselectedColor = Color.White
                )
            )
            Text(text = selectedType, style = Typography.bodyMedium.copy(color = Color.White))
        }

        Box(
            modifier = Modifier
                .size(32.dp)
                .background(SecondaryDark, CircleShape)
                .clickable { onSelected(false) },
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Red)
        }
    }
}

@Composable
private fun SelectOption(onSelectedType: (String) -> Unit, onSelected: (Boolean) -> Unit) {
    listOf(
        "Encouragement",
        "Empathy",
        "Validation",
        "Check-In",
        "Share Resources",
        "Other"
    ).forEach { type ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onSelectedType(type)
                    onSelected(true)
                }
        ) {
            RadioButton(
                selected = false,
                onClick = { },
                colors = RadioButtonDefaults.colors(
                    selectedColor = PurplePrimary,
                    unselectedColor = Color.White
                )
            )
            Text(text = type, style = Typography.bodyMedium.copy(color = Color.White))
        }
    }
}

@Composable
private fun CommentContent(commenter: UserEntityFirebase?, comment: VideoComment) {
    Text(
        text = "Comment you're supporting:",
        style = Typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(horizontal = 8.dp)
    )

    Box(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .background(SecondaryDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = commenter?.avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .gradientCircleBorder(),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = commenter?.username ?: "",
                    style = Typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.comment),
                    contentDescription = "Comment",
                    modifier = Modifier
                        .size(16.dp)
                        .drawGradient()
                )
                ExpandableAutoDetectClickableText(
                    fullText = comment.comment,
                    style = Typography.bodyLarge.copy(color = Color.White),
                    hasPadding = false
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.time),
                    contentDescription = "Comment",
                    modifier = Modifier
                        .size(16.dp)
                        .drawGradient()
                )
                Text(
                    text = formatTimeAgo(comment.timestamp),
                    style = Typography.bodySmall.copy(color = GrayTextColor)
                )
            }
        }
    }

    Text(
        text = "*This message will be shown in a supportive context.",
        style = Typography.bodySmall.copy(fontStyle = FontStyle.Italic, color = GrayTextColor),
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@Composable
private fun TitleAndSubtitleAndDescription() {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Offer Support",
            style = Typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Black)
        )
        Text(
            text = "Send a supportive message to the comment",
            style = Typography.bodyMedium.copy(color = Color.White, textAlign = TextAlign.Center)
        )
        Text(
            text = "A kind message can make a difference.",
            style = Typography.bodySmall.copy(color = GrayTextColor, textAlign = TextAlign.Center)
        )
    }
}

private fun getSupportSuggestionMessages(): List<SupportMessageGroup> {
    return listOf(
        SupportMessageGroup(
            SupportType.ENCOURAGEMENT,
            listOf(
                "You're not alone in this.",
                "Take it one step at a time.",
                "You are stronger than you think."
            )
        ),
        SupportMessageGroup(
            SupportType.EMPATHY,
            listOf(
                "I'm really sorry you're going through this.",
                "That sounds really difficult."
            )
        ),
        SupportMessageGroup(
            SupportType.VALIDATION,
            listOf(
                "Your feelings are valid.",
                "What you're feeling makes sense."
            )
        ),
        SupportMessageGroup(
            SupportType.CHECK_IN,
            listOf(
                "How are you feeling right now?",
                "I'm here to listen if you want to talk."
            )
        ),
        SupportMessageGroup(
            SupportType.SHARE_RESOURCES,
            listOf(
                "You don't have to go through this alone.",
                "Reaching out for support can really help."
            )
        )
    )
}