package com.emc.moodmingle.ui.create.post.dialogs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.settings.PostSettings
import com.emc.moodmingle.domain.remote.model.post.settings.PostTemporary
import com.emc.moodmingle.ui.create.post.CreatePostDialogHeader
import com.emc.moodmingle.ui.create.post.settings.activity.ActivityDialog
import com.emc.moodmingle.ui.create.post.settings.audience.AudienceDialog
import com.emc.moodmingle.ui.create.post.settings.block.BlockPeopleViewingDialog
import com.emc.moodmingle.ui.create.post.settings.disable.DisableCommentReactionDialog
import com.emc.moodmingle.ui.create.post.settings.keyword.FilterKeywordDialog
import com.emc.moodmingle.ui.create.post.settings.pin.PinDialog
import com.emc.moodmingle.ui.create.post.settings.schedule.ScheduleDialog
import com.emc.moodmingle.ui.create.post.settings.sensitive.SensitiveContentFlagDialog
import com.emc.moodmingle.ui.create.post.settings.temporary.TemporaryDialog
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun CreatePostSettingsDialog(
    postSettings: PostSettings,
    onSettingsCreated: (PostSettings) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedSettings by remember { mutableStateOf("") }

    val clearSelectedSettings = { selectedSettings = "" }

    BackHandler { if (selectedSettings.isNotBlank()) clearSelectedSettings() else onDismiss() }

    Box {
        Scaffold(
            containerColor = Color.Black,
            topBar = { CreatePostDialogHeader(label = "Settings", onBack = { onDismiss() }) },
        ) { paddingValues ->
            SettingsDialogContent(
                paddingValues,
                audience = postSettings.audience,
                temporary = postSettings.temporary,
                isPinned = postSettings.pinned,
                onSelectedSettings = { selectedSettings = it }
            )
        }

        if (selectedSettings.isNotBlank()) {
            when (selectedSettings) {
                "Audience" -> {
                    AudienceDialog(
                        audience = postSettings.audience,
                        onAudienceSelected = { onSettingsCreated(postSettings.copy(audience = it)) },
                        onDismiss = clearSelectedSettings
                    )
                }

                "Activity" -> {
                    ActivityDialog(
                        activity = postSettings.activity,
                        onActivitySelected = { onSettingsCreated(postSettings.copy(activity = it)) },
                        onDismiss = clearSelectedSettings
                    )
                }

                "Schedule Expiration" -> {
                    ScheduleDialog(
                        onScheduleCreated = { onSettingsCreated(postSettings.copy(postSchedule = it)) },
                        onDismiss = clearSelectedSettings
                    )
                }

                "Temporary" -> {
                    TemporaryDialog(
                        temporary = postSettings.temporary,
                        onTemporaryChanged = { onSettingsCreated(postSettings.copy(temporary = it)) },
                        onDismiss = clearSelectedSettings
                    )
                }

                "Pin" -> {
                    PinDialog(
                        isPinned = postSettings.pinned,
                        onPinChanged = { onSettingsCreated(postSettings.copy(pinned = it)) },
                        onDismiss = clearSelectedSettings
                    )
                }

                "Disable Comment/Reaction" -> {
                    DisableCommentReactionDialog(
                        commentReactionVisibility = postSettings.commentReactionVisibility,
                        onCommentReactionVisibility = {
                            onSettingsCreated(postSettings.copy(commentReactionVisibility = it))
                        },
                        onDismiss = clearSelectedSettings
                    )
                }

                "Keyword Filtering" -> {
                    FilterKeywordDialog(
                        filteredKeywords = postSettings.filteredKeywords,
                        onKeywordsFiltered = { onSettingsCreated(postSettings.copy(filteredKeywords = it)) },
                        onDismiss = clearSelectedSettings
                    )
                }

                "Sensitive Content Flag" -> {
                    SensitiveContentFlagDialog(
                        isSensitiveFlagEnabled = postSettings.sensitiveFlagEnabled,
                        onSensitiveFlagChanged = {
                            onSettingsCreated(postSettings.copy(sensitiveFlagEnabled = it))
                        },
                        onDismiss = clearSelectedSettings
                    )
                }

                "Block People from Viewing" -> {
                    BlockPeopleViewingDialog(
                        blockedUserIds = postSettings.blockedUserIds,
                        onUsersBlocked = { onSettingsCreated(postSettings.copy(blockedUserIds = it)) },
                        onDismiss = clearSelectedSettings
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsDialogContent(
    paddingValues: PaddingValues,
    audience: Any,
    temporary: PostTemporary,
    isPinned: Boolean,
    onSelectedSettings: (String) -> Unit
) {
    val settingsActions = getSettingsActions()

    Column(modifier = Modifier.padding(paddingValues)) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(settingsActions) { (title, settings) ->
                SettingsItem(
                    title,
                    settings,
                    audience,
                    temporary,
                    isPinned,
                    onSelectedSettings
                )
            }
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    settings: List<Pair<Int, String>>,
    audience: Any,
    temporary: PostTemporary,
    isPinned: Boolean,
    onSelectedSettings: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ItemTitle(title)

        Column {
            settings.forEach { (icon, label) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .clickable { onSelectedSettings(label) }
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ItemIcon(icon, label)
                        ItemLabel(label, audience, temporary, isPinned)
                    }

                    ItemRightIcon()
                }
            }
        }
    }
}

@Composable
private fun ItemTitle(label: String) {
    Text(
        text = label,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp)
    )
}

@Composable
private fun ItemLabel(label: String, audience: Any, temporary: PostTemporary, isPinned: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = if (label == "Pin" && isPinned) "Pinned" else label,
            style = Typography.bodyMedium.copy(color = Color.White)
        )

        Text(
            text = when (label) {
                "Audience" -> "(${audience as? String ?: "Custom"})"
                "Temporary" -> "(${if (temporary.enabled) "Enabled" else "Disabled"})"
                else -> ""
            },
            style = Typography.bodySmall
        )
    }
}

@Composable
private fun ItemIcon(icon: Int, label: String) {
    Box(
        modifier = Modifier
            .background(PrimaryDark, CircleShape)
            .padding(12.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = label,
            modifier = Modifier
                .size(24.dp)
                .drawGradient()
        )
    }
}

@Composable
private fun ItemRightIcon() {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = "Right",
        tint = Color.White,
        modifier = Modifier.size(32.dp)
    )
}

private fun getSettingsActions(): List<Pair<String, List<Pair<Int, String>>>> {
    return listOf(
        "Context & Social Signals" to listOf(
            R.drawable.audience to "Audience",
            R.drawable.activity to "Activity",
        ),
        "Timing & Visibility" to listOf(
            R.drawable.schedule to "Schedule Expiration",
            R.drawable.temporary to "Temporary",
            R.drawable.pin to "Pin"
        ),
        "Privacy & Safety" to listOf(
            R.drawable.blocked to "Disable Comment/Reaction",
            R.drawable.filter to "Keyword Filtering",
            R.drawable.flag to "Sensitive Content Flag",
            R.drawable.block_user to "Block People from Viewing"
        )
    )
}