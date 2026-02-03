package com.emc.moodmingle.ui.video.comment.more.secondary.trigger

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.data.firebase.model.video.VideoComment
import com.emc.moodmingle.data.model.post.formatTimeAgo
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.modifier.roundedGrayBorder
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@Composable
fun BoxScope.TriggerSecondPage(comment: VideoComment, onSelectedTriggerOption: (String) -> Unit) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val commenter by remember(comment.commenterId) {
        userViewModel.getUserById(comment.commenterId)
    }.collectAsState(initial = null)

    Column(
        modifier = Modifier
            .align(Alignment.Center)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onError)
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CommenterAvatar(commenter)
                        CommenterUsernameAndTimestamp(commenter, comment)
                    }

                    CommentText(comment)
                }
            }
        }

        TriggerOptions(onSelectedTriggerOption)
        FooterInfo()
    }
}

@Composable
private fun CommenterAvatar(commenter: UserEntityFirebase?) {
    AsyncImage(
        model = commenter?.avatarUrl,
        contentDescription = "Avatar",
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun CommenterUsernameAndTimestamp(commenter: UserEntityFirebase?, comment: VideoComment) {
    Column {
        Text(text = commenter?.username ?: "", maxLines = 1, overflow = TextOverflow.Ellipsis)

        Text(
            text = formatTimeAgo(comment.timestamp),
            style = Typography.bodySmall.copy(color = GrayTextColor)
        )
    }
}

@Composable
private fun CommentText(comment: VideoComment) {
    Box(
        modifier = Modifier.background(
            MaterialTheme.colorScheme.errorContainer,
            RoundedCornerShape(8.dp)
        )
    ) {
        Text(
            text = comment.comment,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun TriggerOptions(onSelectedTriggerOption: (String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var customTrigger by remember { mutableStateOf("") }

    Column {
        var selectedOption by remember { mutableStateOf("") }

        Text(
            text = "Select the option that best describes how this content affects your or others.",
            style = Typography.bodyMedium.copy(color = Color.White)
        )

        listOf(
            "Violence or Threats",
            "Hateful or Harassing Language",
            "Sexual or Explicit Content",
            "Self-hard or Suicide References",
            "Trauma-related Content",
            "Discrimination",
            "Other (Please specify)"
        ).forEach { text ->
            val isSelected = selectedOption == text

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = isSelected,
                    onClick = {
                        selectedOption = text

                        if (text == "Other (Please specify)") {
                            showDialog = true
                        } else {
                            onSelectedTriggerOption(selectedOption)
                        }
                    }
                )

                Text(
                    text = if (text == "Other (Please specify)" && customTrigger.isNotBlank()) {
                        text.replace("Other (Please specify)", customTrigger)
                    } else {
                        text
                    },
                    style = Typography.bodyMedium.copy(color = Color.White),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    if (showDialog) {
        CustomTriggerBottomSheet(
            onValueChange = {
                customTrigger = it
                onSelectedTriggerOption(it)
            },
            onDismiss = { showDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomTriggerBottomSheet(onValueChange: (String) -> Unit, onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = PrimaryDark,
        dragHandle = { BottomSheetDefaults.DragHandle(modifier = Modifier.drawGradient()) }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomInputField(onValueChange)
        }
    }
}

@Composable
fun CustomInputField(onValueChange: (String) -> Unit) {
    var value by remember { mutableStateOf("") }

    TextField(
        value = value,
        onValueChange = {
            value = it
            onValueChange(value)
        },
        placeholder = {
            Text(
                text = "Enter custom trigger content...",
                style = Typography.bodyMedium
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = SecondaryDark,
            unfocusedPlaceholderColor = GrayTextColor,
            focusedTextColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            focusedContainerColor = SecondaryDark,
            cursorColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .roundedGrayBorder(8.dp)
    )
}

@Composable
private fun FooterInfo() {
    Text(
        text = "People will be able to see the option you selected.",
        textAlign = TextAlign.Center,
        style = Typography.bodySmall.copy(color = GrayTextColor)
    )
}