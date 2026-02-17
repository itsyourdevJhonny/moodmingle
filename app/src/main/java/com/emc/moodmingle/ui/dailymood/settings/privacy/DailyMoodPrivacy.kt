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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.DailyMoodPrivacy
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.MentionTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
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
    LazyColumn(modifier = Modifier.padding(paddingValues)) {
        items(getDailyMoodSettingsActions()) { actionGroup ->
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
        Column(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = actionGroup.groupName,
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )

            HorizontalDivider(thickness = 0.5.dp)
        }

        actionGroup.actions.forEach { action ->
            TextButton(
                onClick = { onActionSelected(action.title) },
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
                contentPadding = PaddingValues(16.dp),
                shape = RectangleShape
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(action.icon),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )

                        Text(text = action.title)
                    }

                    Text(
                        text = action.description,
                        color = GrayTextColor,
                        style = Typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 28.dp)
                    )
                }
            }

            HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 20.dp))

            when (action.title) {
                "Hide Daily Mood from People" -> {
                    HiddenPeople(privacy, userViewModel, onActionSelected)
                }
            }
        }
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