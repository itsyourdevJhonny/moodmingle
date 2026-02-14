package com.emc.moodmingle.ui.dailymood.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
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
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodSettings
import com.emc.moodmingle.utils.components.ScaffoldHeader

@Composable
fun DailyMoodSettings(
    mood: DailyMoodEntity,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
    onDismiss: () -> Unit,
) {
    val originalSettings = remember(Unit) { mood.settings }

    var selectedSetting by remember { mutableStateOf(Settings.DEFAULT) }

    BackHandler { onDismiss() }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            ScaffoldHeader(
                title = "Story Settings",
                doneLabel = "Apply Changes",
                enabled = originalSettings != mood.settings,
                onDone = {
                    onSettingsEdited(mood.settings.copy())
                },
                onBack = { onDismiss() }
            )
        }
    ) { paddingValues ->
        Content(paddingValues) { selectedSetting = it }
    }

    when (selectedSetting) {
        Settings.DEFAULT -> {
            onSettingsEdited(DailyMoodSettings())
        }

        Settings.TIMING -> {
            DailyMoodTimingSetting()
        }

        Settings.SHARE_PLATFORM -> {}
        Settings.NOTIFY -> {}
    }
}

@Composable
fun DailyMoodTimingSetting() {
    var selectedTiming by remember { mutableStateOf("") }

    Column {
        listOf("Auto Post Now", "Schedule", "Manual Only").forEach {
            val isSelected = it == selectedTiming

            TimingItem(title = it, isSelected, onClick = { selectedTiming = it })
        }
    }
}

@Composable
fun TimingItem(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title)

        RadioButton(selected = isSelected, onClick = null)
    }
}

@Composable
private fun Content(paddingValues: PaddingValues, onSettingSelected: (Settings) -> Unit) {
    Column(modifier = Modifier.padding(paddingValues)) {
        getDailyMoodSettings().forEach { (setting, icon) ->
            SettingItem(
                setting = setting,
                icon = icon,
                onSelected = { onSettingSelected(setting) }
            )
        }
    }
}

@Composable
private fun SettingItem(setting: Settings, icon: Int, onSelected: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable { onSelected() }
            .padding(12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingIcon(icon = icon)
            SettingTitle(title = setting.name)
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
fun SettingIcon(icon: Int) {
    Icon(
        painter = painterResource(icon),
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
fun SettingTitle(title: String) {
    Text(
        text = title.lowercase().replaceFirstChar { it.uppercase() }
            .split("_")[1].replaceFirstChar { it.uppercase() },
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}

private fun getDailyMoodSettings(): List<Pair<Settings, Int>> {
    return listOf(
        Settings.DEFAULT to R.drawable.settings_default,
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