package com.emc.moodmingle.ui.dailymood.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.domain.remote.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.ui.dailymood.settings.action.Action
import com.emc.moodmingle.ui.dailymood.settings.action.ActionGroup
import com.emc.moodmingle.ui.dailymood.settings.action.getDailyMoodSettingsActions
import com.emc.moodmingle.ui.dailymood.settings.archive.AutoArchiveScreen
import com.emc.moodmingle.ui.dailymood.settings.autosave.AutoSaveToDeviceScreen
import com.emc.moodmingle.ui.dailymood.settings.block.BlockedPeopleScreen
import com.emc.moodmingle.ui.dailymood.settings.download.AllowDownloadsScreen
import com.emc.moodmingle.ui.dailymood.settings.duration.MoodDurationScreen
import com.emc.moodmingle.ui.dailymood.settings.ghostmode.GhostModeScreen
import com.emc.moodmingle.ui.dailymood.settings.hide.HideMoodFromPeopleScreen
import com.emc.moodmingle.ui.dailymood.settings.notify.NotifyPeopleScreen
import com.emc.moodmingle.ui.dailymood.settings.quality.UploadQualityScreen
import com.emc.moodmingle.ui.dailymood.settings.reaction.ReactionSettingsScreen
import com.emc.moodmingle.ui.dailymood.settings.replay.ReplayLimitScreen
import com.emc.moodmingle.ui.dailymood.settings.reply.ReplyPermissionScreen
import com.emc.moodmingle.ui.dailymood.settings.restrict.RestrictAccountsScreen
import com.emc.moodmingle.ui.dailymood.settings.screenprotection.ScreenProtectionScreen
import com.emc.moodmingle.ui.dailymood.settings.screenshot.ScreenshotAlertScreen
import com.emc.moodmingle.ui.dailymood.settings.shareplatform.SharePlatformScreen
import com.emc.moodmingle.ui.dailymood.settings.sharing.SharingForwardingScreen
import com.emc.moodmingle.ui.dailymood.settings.timing.TimingScreen
import com.emc.moodmingle.ui.dailymood.settings.util.SettingsScreenType
import com.emc.moodmingle.ui.dailymood.settings.util.toSettingsScreenType
import com.emc.moodmingle.ui.dailymood.settings.util.toTitle
import com.emc.moodmingle.ui.dailymood.settings.viewlist.ViewListVisibilityScreen
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.ScaffoldHeader
import com.emc.moodmingle.utils.components.UserSelector
import com.emc.moodmingle.utils.modifier.drawGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyMoodSettingsScreen(
    settings: DailyMoodSettings,
    onEdit: (DailyMoodSettings) -> Unit,
    onDismiss: () -> Unit,
) {
    var currentScreen by remember { mutableStateOf<SettingsScreenType?>(null) }

    BackHandler { if (currentScreen != null) currentScreen = null else onDismiss() }

    Box {
        Scaffold(
            containerColor = Color.Black,
            topBar = {
                ScaffoldHeader(
                    title = "Story Settings",
                    doneLabel = "Apply Changes",
                    onBack = { onDismiss() }
                )
            },
            content = { paddingValues -> Content(paddingValues) { currentScreen = it } }
        )

        currentScreen?.let { screen ->
            CurrentScreen(settings, screen, onEdit) { currentScreen = null }
        }
    }
}

@Composable
private fun Content(paddingValues: PaddingValues, onActionSelected: (SettingsScreenType) -> Unit) {
    LazyColumn(modifier = Modifier.padding(paddingValues)) {
        items(getDailyMoodSettingsActions()) { actionGroup ->
            SettingsGroup(actionGroup, onActionSelected)
        }
    }
}

@Composable
private fun CurrentScreen(
    settings: DailyMoodSettings,
    screen: SettingsScreenType?,
    onEdit: (DailyMoodSettings) -> Unit,
    onBack: () -> Unit,
) {
    var openSelector by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Black,
        topBar = { ScaffoldHeader(title = screen?.toTitle().orEmpty()) { onBack() } }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .animateContentSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            InvokeScreen(
                settings,
                screen,
                onPeopleSelectorOpen = { openSelector = true },
                onEdit,
                onBack
            )
        }
    }

    if (openSelector) {
        UserSelector(
            title = "Hide mood from",
            userIds = settings.hiddenUserIds,
            onUsersSelected = { selected ->
                val selectedUserIds = (selected as SnapshotStateList<*>).map { it.toString() }
                onEdit(
                    when (screen) {
                        SettingsScreenType.HideMood -> settings.copy(hiddenUserIds = selectedUserIds)
                        SettingsScreenType.BlockedPeople -> settings.copy(blockedUserIds = selectedUserIds)
                        SettingsScreenType.RestrictedAccounts -> settings.copy(restrictedUserIds = selectedUserIds)
                        else -> settings
                    }
                )
                openSelector = false
            },
            onDismiss = { openSelector = false }
        )
    }
}

@Composable
private fun InvokeScreen(
    settings: DailyMoodSettings,
    screen: SettingsScreenType?,
    onPeopleSelectorOpen: () -> Unit,
    onEdit: (DailyMoodSettings) -> Unit,
    onBack: () -> Unit,
) {
    val screenMap: Map<SettingsScreenType, @Composable (DailyMoodSettings, (DailyMoodSettings) -> Unit, () -> Unit) -> Unit> =
        mapOf(
            SettingsScreenType.ViewListVisibility to { settings, onEdit, onBack ->
                ViewListVisibilityScreen(settings, onEdit)
            },
            SettingsScreenType.ScreenshotAlerts to { settings, onEdit, onBack ->
                ScreenshotAlertScreen(settings, onEdit)
            },
            SettingsScreenType.ReplayLimit to { settings, onEdit, onBack ->
                ReplayLimitScreen(settings, onEdit)
            },
            SettingsScreenType.HideMood to { settings, onEdit, onBack ->
                HideMoodFromPeopleScreen(settings, onPeopleSelectorOpen, onEdit)
            },
            SettingsScreenType.NotifyPeople to { settings, onEdit, onBack ->
                NotifyPeopleScreen(settings, onEdit)
            },
            SettingsScreenType.Timing to { settings, onEdit, onBack ->
                TimingScreen(settings, onEdit)
            },
            SettingsScreenType.ReplyPermissions to { settings, onEdit, onBack ->
                ReplyPermissionScreen(settings, onEdit)
            },
            SettingsScreenType.ReactionSettings to { settings, onEdit, onBack ->
                ReactionSettingsScreen(settings, onEdit)
            },
            SettingsScreenType.SharingForwarding to { settings, onEdit, onBack ->
                SharingForwardingScreen(settings, onEdit)
            },
            SettingsScreenType.BlockedPeople to { settings, onEdit, onBack ->
                BlockedPeopleScreen(settings, onPeopleSelectorOpen, onEdit)
            },
            SettingsScreenType.RestrictedAccounts to { settings, onEdit, onBack ->
                RestrictAccountsScreen(settings, onPeopleSelectorOpen, onEdit)
            },
            SettingsScreenType.MoodDuration to { settings, onEdit, onBack ->
                MoodDurationScreen(settings, onEdit)
            },
            SettingsScreenType.AutoArchive to { settings, onEdit, onBack ->
                AutoArchiveScreen(settings, onEdit)
            },
            SettingsScreenType.SharePlatform to { settings, onEdit, onBack ->
                SharePlatformScreen(settings, onEdit)
            },
            SettingsScreenType.AllowDownloads to { settings, onEdit, onBack ->
                AllowDownloadsScreen(settings, onEdit)
            },
            SettingsScreenType.AutoSaveDevice to { settings, onEdit, onBack ->
                AutoSaveToDeviceScreen(settings, onEdit)
            },
            SettingsScreenType.UploadQuality to { settings, onEdit, onBack ->
                UploadQualityScreen(settings, onEdit)
            },
            SettingsScreenType.GhostMode to { settings, onEdit, onBack ->
                GhostModeScreen(settings, onEdit)
            },
            SettingsScreenType.ScreenProtection to { settings, onEdit, onBack ->
                ScreenProtectionScreen(settings, onEdit)
            }
        )

    screen?.let { screenMap[it]?.invoke(settings, onEdit, onBack) }
}

@Composable
private fun SettingsGroup(
    actionGroup: ActionGroup,
    onActionSelected: (SettingsScreenType) -> Unit,
) {
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
private fun SettingItem(action: Action, onActionSelected: (SettingsScreenType) -> Unit) {
    TextButton(
        onClick = { onActionSelected(action.title.toSettingsScreenType()!!) },
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