package com.emc.moodmingle.ui.dailymood.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodSettings
import com.emc.moodmingle.data.firebase.model.post.dailymood.NotifyType
import com.emc.moodmingle.data.firebase.model.post.dailymood.SharePlatformType
import com.emc.moodmingle.data.firebase.model.post.dailymood.TimingType
import com.emc.moodmingle.ui.dailymood.settings.timing.DailyMoodManualDialog
import com.emc.moodmingle.ui.dailymood.settings.timing.DailyMoodScheduleDialog
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.ScaffoldHeader

@Composable
fun DailyMoodSettings(
    settings: DailyMoodSettings,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
    onDismiss: () -> Unit,
) {
    val originalSettings = remember(Unit) { settings }
    val hasChanges = originalSettings != settings

    var selectedSetting by remember { mutableStateOf(Settings.DEFAULT) }
    var timingAction by remember { mutableStateOf("") }

    BackHandler { onDismiss() }

    Box {
        Scaffold(
            containerColor = Color.Black,
            topBar = {
                ScaffoldHeader(
                    title = "Story Settings",
                    doneLabel = "Apply Changes",
                    enabled = hasChanges,
                    onDone = {},
                    onBack = { onDismiss() }
                )
            },
            floatingActionButton = {
                TextButton(
                    onClick = { onSettingsEdited(DailyMoodSettings()) },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.settings_default),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(text = " Reset To Default")
                }
            }
        ) { paddingValues ->
            Content(paddingValues, settings) {
                selectedSetting = it
            }
        }

        when (selectedSetting) {
            Settings.TIMING -> {
                SettingContainer(
                    title = "Select Timing",
                    contents = listOf(
                        R.drawable.automatic to TimingType.AUTO_POST_NOW,
                        R.drawable.schedule to TimingType.SCHEDULE,
                        R.drawable.manual to TimingType.MANUAL_ONLY
                    ),
                    type = settings.timing.type,
                    onSelected = {
                        when (it as TimingType) {
                            TimingType.AUTO_POST_NOW -> {
                                onSettingsEdited(
                                    settings.copy(
                                        timing = settings.timing.copy(type = it, date = null)
                                    )
                                )
                            }

                            TimingType.SCHEDULE, TimingType.MANUAL_ONLY -> timingAction = it.name
                        }
                    },
                    onDismiss = { selectedSetting = Settings.DEFAULT }
                )
            }

            Settings.SHARE_PLATFORM -> {
                SettingContainer(
                    title = "Select Platform",
                    contents = listOf(
                        R.drawable.facebook to SharePlatformType.FACEBOOK,
                        R.drawable.instragram to SharePlatformType.INSTAGRAM,
                        R.drawable.x_black to SharePlatformType.X,
                        R.drawable.threads_black to SharePlatformType.THREADS
                    ),
                    type = settings.sharePlatform,
                    isImage = true,
                    size = 42.dp,
                    onSelected = { onSettingsEdited(settings.copy(sharePlatform = (it as SharePlatformType))) },
                    onDismiss = { selectedSetting = Settings.DEFAULT }
                )
            }

            Settings.NOTIFY -> {
                SettingContainer(
                    title = "Notify To",
                    contents = listOf(
                        R.drawable.blocked to NotifyType.NONE,
                        R.drawable.followers to NotifyType.FOLLOWERS,
                        R.drawable.supporter to NotifyType.SUPPORTERS,
                    ),
                    type = settings.notify,
                    onSelected = { onSettingsEdited(settings.copy(notify = (it as NotifyType))) },
                    onDismiss = { selectedSetting = Settings.DEFAULT }
                )
            }

            else -> {}
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
                onManualCreated = { date ->
                    onSettingsEdited(
                        settings.copy(
                            timing = settings.timing.copy(type = TimingType.MANUAL_ONLY, date)
                        )
                    )
                },
                onDismiss = { timingAction = "" }
            )
        }
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    settings: DailyMoodSettings,
    onSettingSelected: (Settings) -> Unit,
) {
    Column(modifier = Modifier.padding(paddingValues)) {
        getDailyMoodSettings().forEach { (setting, icon) ->
            SettingItem(settings, setting, icon) { onSettingSelected(setting) }
        }
    }
}

@Composable
private fun SettingItem(
    settings: DailyMoodSettings,
    setting: Settings,
    icon: Int,
    onSelected: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onSelected() }
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingIcon(icon = icon)
            SettingTitle(title = setting.name, settings)
        }

        Icon(
            painter = painterResource(R.drawable.chevron_right),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingIcon(icon: Int) {
    Icon(
        painter = painterResource(icon),
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
private fun SettingTitle(title: String, settings: DailyMoodSettings) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = when (title) {
                "TIMING" -> "Timing"
                "SHARE_PLATFORM" -> "Share Platform"
                "NOTIFY" -> "Notify"
                else -> title
            },
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "(${
                when (title) {
                    "TIMING" -> when (settings.timing.type) {
                        TimingType.AUTO_POST_NOW -> "Auto"
                        TimingType.SCHEDULE -> "Scheduled"
                        TimingType.MANUAL_ONLY -> "Manual"
                    }

                    "SHARE_PLATFORM" -> settings.sharePlatform.name.lowercase()
                        .replaceFirstChar { it.titlecase() }

                    "NOTIFY" -> settings.notify.name.lowercase().replaceFirstChar { it.titlecase() }
                    else -> title
                }
            })",
            style = Typography.bodyMedium.copy(color = GrayTextColor)
        )
    }
}

private fun getDailyMoodSettings(): List<Pair<Settings, Int>> {
    return listOf(
        Settings.TIMING to R.drawable.timing,
        Settings.SHARE_PLATFORM to R.drawable.share_platform,
        Settings.NOTIFY to R.drawable.notify,
    )
}

private enum class Settings {
    DEFAULT,
    TIMING,
    SHARE_PLATFORM,
    NOTIFY,
}