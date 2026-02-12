package com.emc.moodmingle.ui.dailymood.mention

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.post.action.toastMessage
import com.emc.moodmingle.ui.theme.MentionTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.utils.components.ScaffoldHeader
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@Composable
fun DailyMoodMentionSection(mood: DailyMoodEntity, onMentionDeleted: (String) -> Unit) {
    var showMentionDialog by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = mood.text.mentions.isNotEmpty(),
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.padding(start = 16.dp)
    ) {
        Row {
            MentionButton { showMentionDialog = true }
            TotalMention(size = mood.text.mentions.size)
        }
    }

    if (showMentionDialog) {
        MentionDialog(mood, onMentionDeleted) { showMentionDialog = false }
    }
}

@Composable
private fun MentionDialog(
    mood: DailyMoodEntity,
    onMentionDeleted: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            containerColor = Color.Black,
            topBar = {
                ScaffoldHeader(title = "Your Mentions (${mood.text.mentions.size})") {
                    onDismiss()
                }
            }
        ) { paddingValues ->
            DialogContent(paddingValues, mood, onMentionDeleted)
        }
    }
}

@Composable
fun DialogContent(
    paddingValues: PaddingValues,
    mood: DailyMoodEntity,
    onMentionDeleted: (String) -> Unit,
) {
    val context = LocalContext.current
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()

    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(top = 8.dp)
    ) {
        items(items = mood.text.mentions, key = { it }) { userId ->
            val user = userViewModel.getUserById(userId).collectAsState(initial = null).value

            user?.let {
                MentionItem(user = it) {
                    onMentionDeleted(user.uid)
                    toastMessage(context, "Mention deleted")
                }
            }
        }
    }
}

@Composable
private fun LazyItemScope.MentionItem(user: UserEntityFirebase, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
            .animateItem()
    ) {
        ItemAvatarAndUsername(user)
        ItemRemoveButton(onClick)
    }
}

@Composable
private fun ItemAvatarAndUsername(user: UserEntityFirebase) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Text(
            text = user.username,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 232.dp)
        )
    }
}

@Composable
private fun ItemRemoveButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(R.drawable.remove),
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun MentionButton(onClick: () -> Unit) {
    IconButton(
        onClick = { onClick() },
        colors = IconButtonDefaults.iconButtonColors(containerColor = SecondaryDark)
    ) {
        Icon(
            painter = painterResource(R.drawable.mention),
            contentDescription = null,
            tint = MentionTextColor,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
private fun TotalMention(size: Int) {
    Text(
        text = "$size",
        color = Color.White,
        modifier = Modifier
            .offset(x = (-10).dp)
            .background(MentionTextColor, CircleShape)
            .padding(horizontal = 6.dp)
    )
}