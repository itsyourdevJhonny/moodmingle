package com.emc.moodmingle.ui.dailymood.settings.privacy

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodPrivacy
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.MentionTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.components.ScaffoldHeader
import com.emc.moodmingle.utils.components.UserSelectorDialog
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@Composable
fun DailyMoodPrivacy(
    privacy: DailyMoodPrivacy,
    onPrivacyChanged: (DailyMoodPrivacy) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()

    var selectedAction by remember { mutableStateOf("") }

    BackHandler { onBack() }

    Box {
        Scaffold(
            containerColor = Color.Black,
            topBar = { ScaffoldHeader(title = "Privacy") { onBack() } }
        ) { paddingValues ->
            Content(paddingValues, privacy, userViewModel) { selectedAction = it }
        }

        when (selectedAction) {
            "Hide Daily Mood from People" -> {
                UserSelectorDialog(
                    headerLabel = "Select People",
                    userIds = privacy.hiddenUsers,
                    onUsersSelected = { result ->
                        val userIds = (result as SnapshotStateList<*>).map { it.toString() }

                        if (userIds != privacy.hiddenUsers) {
                            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                        }

                        onPrivacyChanged(privacy.copy(hiddenUsers = userIds))
                    },
                    onDismiss = { selectedAction = "" }
                )
            }

            "view_hidden_people" -> {
                ViewHiddenPeople(privacy, userViewModel, onPrivacyChanged) { selectedAction = "" }
            }
        }
    }
}

@Composable
fun ViewHiddenPeople(
    privacy: DailyMoodPrivacy,
    userViewModel: FirebaseUserViewModel,
    onPrivacyChanged: (DailyMoodPrivacy) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = {}) {
        Column(
            modifier = Modifier
                .heightIn(max = 580.dp)
                .background(PrimaryDark, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Hidden People (${privacy.hiddenUsers.size})",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.Red
                    )
                }
            }

            if (privacy.hiddenUsers.size > 1) {
                TextButton(
                    onClick = {
                        onPrivacyChanged(privacy.copy(hiddenUsers = emptyList()))
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MentionTextColor),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.view),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = "Unhide All")
                }
            }

            HorizontalDivider(thickness = 0.5.dp)

            LazyColumn {
                itemsIndexed(
                    items = privacy.hiddenUsers,
                    key = { _, userId -> userId }
                ) { index, userId ->
                    val user =
                        userViewModel.getUserById(userId).collectAsState(initial = null).value

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = if (index == 0) 8.dp else Dp.Unspecified)
                            .animateItem()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AsyncImage(
                                model = user?.avatarUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .gradientCircleBorder()
                            )

                            Text(
                                text = user?.username.orEmpty(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 192.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                onPrivacyChanged(privacy.copy(hiddenUsers = privacy.hiddenUsers - userId))
                                if (privacy.hiddenUsers.size == 1) onDismiss()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    privacy: DailyMoodPrivacy,
    userViewModel: FirebaseUserViewModel,
    onActionSelected: (String) -> Unit,
) {
    Column(modifier = Modifier.padding(paddingValues)) {
        getActions().forEach { actionGroup ->
            ActionItem(onActionSelected, actionGroup, privacy, userViewModel)
        }
    }
}

@Composable
private fun ActionItem(
    onActionSelected: (String) -> Unit,
    actionGroup: ActionGroup,
    privacy: DailyMoodPrivacy,
    userViewModel: FirebaseUserViewModel,
) {
    Column {
        actionGroup.actions.forEach { action ->
            Column {
                TextButton(
                    onClick = { onActionSelected(action.title) },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                    shape = RectangleShape
                ) {
                    Icon(
                        painter = painterResource(action.icon),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = action.title,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(
                    text = action.description,
                    color = GrayTextColor,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            when (action.title) {
                "Hide Daily Mood from People" -> {
                    HiddenPeople(privacy, userViewModel, onActionSelected)
                }
            }
        }

        HorizontalDivider(thickness = 0.5.dp)
    }
}

@Composable
private fun HiddenPeople(
    privacy: DailyMoodPrivacy,
    userViewModel: FirebaseUserViewModel,
    onActionSelected: (String) -> Unit,
) {
    val hiddenUsers = privacy.hiddenUsers

    if (hiddenUsers.isNotEmpty()) {
        Column {
            TextButton(
                onClick = { onActionSelected("view_hidden_people") },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    (if (hiddenUsers.size > 8) hiddenUsers.take(8) else hiddenUsers).forEach { userId ->
                        val user =
                            userViewModel.getUserById(userId).collectAsState(initial = null).value

                        AsyncImage(
                            model = user?.avatarUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .gradientCircleBorder()
                        )
                    }

                    if (hiddenUsers.size > 8) {
                        Text(
                            text = "+ ${hiddenUsers.size - 8}",
                            color = GrayTextColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun getActions(): List<ActionGroup> {
    return listOf(

        ActionGroup(
            groupName = "Viewer Controls",
            actions = listOf(
                Action(
                    icon = R.drawable.view,
                    title = "View List Visibility",
                    description = "Choose whether you can see who viewed your daily moods."
                ),
                Action(
                    icon = R.drawable.screenshot,
                    title = "Screenshot Alerts",
                    description = "Receive a notification when someone screenshots your mood."
                ),
                Action(
                    icon = R.drawable.replay_filled,
                    title = "Replay Limit",
                    description = "Control how many times people can replay your mood."
                ),
                Action(
                    icon = R.drawable.hidden,
                    title = "Hide Mood from Specific People",
                    description = "Select people who won’t be able to see your daily moods."
                )
            )
        ),

        ActionGroup(
            groupName = "Interaction Controls",
            actions = listOf(
                Action(
                    icon = R.drawable.reply,
                    title = "Reply Permissions",
                    description = "Choose who can reply to your daily moods."
                ),
                Action(
                    icon = R.drawable.love,
                    title = "Reaction Settings",
                    description = "Allow or disable reactions to your moods."
                ),
                Action(
                    icon = R.drawable.share,
                    title = "Sharing & Forwarding",
                    description = "Control whether others can share or forward your mood."
                )
            )
        ),

        ActionGroup(
            groupName = "Block & Restrict",
            actions = listOf(
                Action(
                    icon = R.drawable.block_filled,
                    title = "Blocked Users",
                    description = "Manage people who are blocked from viewing or interacting with your moods."
                ),
                Action(
                    icon = R.drawable.restrict_filled,
                    title = "Restricted Accounts",
                    description = "Limit certain people’s interactions without them knowing."
                )
            )
        ),

        ActionGroup(
            groupName = "Expiration & Archive",
            actions = listOf(
                Action(
                    icon = R.drawable.timer_filled,
                    title = "Mood Duration",
                    description = "Set how long your daily moods remain visible."
                ),
                Action(
                    icon = R.drawable.archive_filled,
                    title = "Auto Archive",
                    description = "Automatically save expired moods to your private archive."
                ),
                Action(
                    icon = R.drawable.delete,
                    title = "Expire Mood Instantly",
                    description = "Remove your active mood immediately."
                )
            )
        ),

        ActionGroup(
            groupName = "Download & Data Controls",
            actions = listOf(
                Action(
                    icon = R.drawable.download,
                    title = "Allow Downloads",
                    description = "Choose whether viewers can download your mood."
                ),
                Action(
                    icon = R.drawable.save_post,
                    title = "Auto-Save to Device",
                    description = "Automatically save posted moods to your device."
                ),
                Action(
                    icon = R.drawable.quality_filled,
                    title = "Upload Quality",
                    description = "Select high quality or data-saving upload mode."
                )
            )
        ),

        ActionGroup(
            groupName = "Stealth & Privacy Modes",
            actions = listOf(
                Action(
                    icon = R.drawable.ghost_filled,
                    title = "Ghost Mode",
                    description = "Hide your online status while viewing moods."
                ),
                Action(
                    icon = R.drawable.private_filled,
                    title = "Private Mood Mode",
                    description = "Share moods with selected people only and restrict forwarding."
                ),
                Action(
                    icon = R.drawable.record_filled,
                    title = "Screen Recording Protection",
                    description = "Prevent screen recording while viewing your mood."
                )
            )
        )
    )
}

data class ActionGroup(
    val groupName: String,
    val actions: List<Action>,
)

data class Action(
    val icon: Int,
    val title: String,
    val description: String,
)