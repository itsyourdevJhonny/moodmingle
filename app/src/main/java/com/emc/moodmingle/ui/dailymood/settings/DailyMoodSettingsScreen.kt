package com.emc.moodmingle.ui.dailymood.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.ui.dailymood.settings.action.Action
import com.emc.moodmingle.ui.dailymood.settings.action.ActionGroup
import com.emc.moodmingle.ui.dailymood.settings.action.getDailyMoodSettingsActions
import com.emc.moodmingle.ui.dailymood.settings.archive.AutoArchiveScreen
import com.emc.moodmingle.ui.dailymood.settings.autosave.AutoSaveToDeviceScreen
import com.emc.moodmingle.ui.dailymood.settings.block.BlockedPeopleScreen
import com.emc.moodmingle.ui.dailymood.settings.download.AllowDownloadsScreen
import com.emc.moodmingle.ui.dailymood.settings.duration.MoodDurationScreen
import com.emc.moodmingle.ui.dailymood.settings.hide.HideMoodFromScreen
import com.emc.moodmingle.ui.dailymood.settings.notify.NotifyPeopleScreen
import com.emc.moodmingle.ui.dailymood.settings.quality.UploadQualityScreen
import com.emc.moodmingle.ui.dailymood.settings.reaction.ReactionSettingsScreen
import com.emc.moodmingle.ui.dailymood.settings.replay.ReplayLimitScreen
import com.emc.moodmingle.ui.dailymood.settings.reply.ReplyPermissionScreen
import com.emc.moodmingle.ui.dailymood.settings.restrict.RestrictAccountsScreen
import com.emc.moodmingle.ui.dailymood.settings.screenshot.ScreenshotAlertScreen
import com.emc.moodmingle.ui.dailymood.settings.shareplatform.SharePlatformScreen
import com.emc.moodmingle.ui.dailymood.settings.sharing.SharingForwardingScreen
import com.emc.moodmingle.ui.dailymood.settings.timing.TimingScreen
import com.emc.moodmingle.ui.dailymood.settings.viewlist.ViewListVisibilityScreen
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.ScaffoldHeader
import com.emc.moodmingle.utils.modifier.drawGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyMoodSettingsScreen(
    settings: DailyMoodSettings,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedAction by remember { mutableStateOf("") }

    BackHandler { if (selectedAction.isNotEmpty()) selectedAction = "" else onDismiss() }

    Box {
        Scaffold(
            containerColor = Color.Black,
            topBar = {
                ScaffoldHeader(
                    title = "Story Settings",
                    doneLabel = "Apply Changes",
                    onBack = { onDismiss() }
                )
            }
        ) { paddingValues ->
            Content(paddingValues) { selectedAction = it }
        }

        when (selectedAction) {
            "View List Visibility" -> {
                ViewListVisibilityScreen(settings, onSettingsEdited) { selectedAction = "" }
            }

            "Screenshot Alerts" -> {
                ScreenshotAlertScreen(settings, onSettingsEdited) { selectedAction = "" }
            }

            "Replay Limit" -> {
                ReplayLimitScreen(settings, onSettingsEdited) { selectedAction = "" }
            }

            "Hide Mood from Specific People" -> {
                HideMoodFromScreen(settings, onSettingsEdited) { selectedAction = "" }
            }

            "Notify People" -> {
                NotifyPeopleScreen(settings, onSettingsEdited) { selectedAction = "" }
            }

            "Timing" -> {
                TimingScreen(settings, onSettingsEdited) { selectedAction = "" }
            }

            "Reply Permissions" -> {
                ReplyPermissionScreen(settings, onSettingsEdited) { selectedAction = "" }
            }

            "Reaction Settings" -> {
                ReactionSettingsScreen(settings, onSettingsEdited) { selectedAction = "" }
            }

            "Sharing & Forwarding" -> {
                SharingForwardingScreen(settings, onSettingsEdited) { selectedAction = "" }
            }

            "Blocked People" -> {
                BlockedPeopleScreen(settings, onSettingsEdited) { selectedAction = "" }
            }

            "Restricted Accounts" -> {
                RestrictAccountsScreen(settings, onSettingsEdited) { selectedAction = "" }
            }

            "Mood Duration" -> {
                MoodDurationScreen(settings, onSettingsEdited) { selectedAction = "" }
            }

            "Auto Archive" -> {
                AutoArchiveScreen(settings, onSettingsEdited) { selectedAction = "" }
            }

            "Share Platform" -> {
                SharePlatformScreen(settings, onSettingsEdited) { selectedAction = "" }
            }

            "Allow Downloads" -> {
                AllowDownloadsScreen(settings, onSettingsEdited) { selectedAction = "" }
            }

            "Auto-Save to Device" -> {
                AutoSaveToDeviceScreen(settings, onSettingsEdited) { selectedAction = "" }
            }

            "Upload Quality" -> {
                UploadQualityScreen(settings, onSettingsEdited) { selectedAction = "" }
            }
        }
    }
}

@Composable
private fun Content(paddingValues: PaddingValues, onActionSelected: (String) -> Unit) {
    LazyColumn(modifier = Modifier.padding(paddingValues)) {
        items(getDailyMoodSettingsActions()) { actionGroup ->
            SettingsGroup(actionGroup, onActionSelected)
        }
    }
}

@Composable
private fun SettingsGroup(actionGroup: ActionGroup, onActionSelected: (String) -> Unit) {
    Column {
        Column(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsGroupIconAndName(actionGroup)
            HorizontalDivider(thickness = 0.5.dp)
        }

        actionGroup.actions.forEachIndexed { index, action ->
            SettingItem(action, onActionSelected)
            SettingBottomLine(index, actionGroup)
        }
    }
}

@Composable
private fun SettingsGroupIconAndName(actionGroup: ActionGroup) {
    Row(
        modifier = Modifier.padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(actionGroup.groudIcon),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .drawGradient()
        )

        Text(
            text = actionGroup.groupName,
            fontSize = 18.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SettingItem(action: Action, onActionSelected: (String) -> Unit) {
    TextButton(
        onClick = { onActionSelected(action.title) },
        colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
        contentPadding = PaddingValues(16.dp),
        shape = RectangleShape
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SettingItemIconAndTitle(action)
            SettingItemDescription(action)
        }
    }
}

@Composable
private fun SettingItemIconAndTitle(action: Action) {
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
}

@Composable
private fun SettingItemDescription(action: Action) {
    Text(
        text = action.description,
        color = GrayTextColor,
        style = Typography.bodySmall,
        modifier = Modifier.padding(horizontal = 28.dp)
    )
}

@Composable
private fun SettingBottomLine(index: Int, actionGroup: ActionGroup) {
    HorizontalDivider(
        thickness = 0.5.dp,
        modifier = Modifier.padding(horizontal = if (index == actionGroup.actions.lastIndex) 0.dp else 20.dp)
    )
}
