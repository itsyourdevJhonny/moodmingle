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
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodSettings
import com.emc.moodmingle.data.firebase.model.post.dailymood.NotifyType
import com.emc.moodmingle.data.firebase.model.post.dailymood.SharePlatformType
import com.emc.moodmingle.data.firebase.model.post.dailymood.TimingType
import com.emc.moodmingle.ui.dailymood.settings.privacy.Action
import com.emc.moodmingle.ui.dailymood.settings.privacy.ActionGroup
import com.emc.moodmingle.ui.dailymood.settings.privacy.getDailyMoodSettingsActions
import com.emc.moodmingle.ui.dailymood.settings.timing.DailyMoodManualDialog
import com.emc.moodmingle.ui.dailymood.settings.timing.DailyMoodScheduleDialog
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.ScaffoldHeader
import com.emc.moodmingle.utils.modifier.drawGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyMoodSettings(
    settings: DailyMoodSettings,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedAction by remember { mutableStateOf("") }
    var timingAction by remember { mutableStateOf("") }

    BackHandler { onDismiss() }

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

            }

            "Share Platform" -> {
                SettingContainer(
                    title = "Select Platform",
                    contents = listOf(
                        R.drawable.facebook to SharePlatformType.FACEBOOK,
                        R.drawable.instragram to SharePlatformType.INSTAGRAM,
                        R.drawable.x_black to SharePlatformType.X,
                        R.drawable.threads_black to SharePlatformType.THREADS
                    ),
                    type = settings.sharePlatformType,
                    isImage = true,
                    size = 42.dp,
                    onSelected = { data ->
                        val isCurrent = (data as SharePlatformType) == settings.sharePlatformType
                        onSettingsEdited(settings.copy(sharePlatformType = if (isCurrent) SharePlatformType.NONE else data))
                    },
                    onDismiss = { selectedAction = "" }
                )
            }

            "Notify People" -> {
                SettingContainer(
                    title = "Notify People",
                    contents = listOf(
                        R.drawable.blocked to NotifyType.NONE,
                        R.drawable.followers to NotifyType.FOLLOWERS,
                        R.drawable.supporter to NotifyType.SUPPORTERS,
                    ),
                    type = settings.notifyType,
                    onSelected = { data -> onSettingsEdited(settings.copy(notifyType = (data as NotifyType))) },
                    onDismiss = { selectedAction = "" }
                )
            }
        }
    }

    when (timingAction.lowercase()) {
        "schedule" -> {
            DailyMoodScheduleDialog(
                timing = settings.timing,
                onScheduleCreated = { date ->
                    val isNullOrCurrent = date == null || date == settings.timing.date

                    onSettingsEdited(
                        settings.copy(
                            timing = settings.timing.copy(
                                type = if (isNullOrCurrent) TimingType.AUTO_POST_NOW else TimingType.SCHEDULE,
                                date = if (isNullOrCurrent) null else date
                            )
                        )
                    )
                },
                onDismiss = { timingAction = "" }
            )
        }

        "manual_only" -> {
            DailyMoodManualDialog(
                onManualCreated = { date, time ->
                    onSettingsEdited(
                        settings.copy(
                            timing = settings.timing.copy(type = TimingType.MANUAL_ONLY, date, time)
                        )
                    )
                },
                onDismiss = { timingAction = "" }
            )
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
