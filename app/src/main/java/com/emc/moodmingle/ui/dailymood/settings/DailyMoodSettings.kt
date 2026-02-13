package com.emc.moodmingle.ui.dailymood.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodSettings
import com.emc.moodmingle.ui.post.action.toastMessage
import com.emc.moodmingle.utils.components.ScaffoldHeader

@Composable
fun DailyMoodSettings(
    mood: DailyMoodEntity,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val originalSettings = remember(Unit) { mood.settings }

    BackHandler { onDismiss() }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            ScaffoldHeader(
                title = "Story Settings",
                doneLabel = "Apply Changes",
                enabled = originalSettings != mood.settings,
                onDone = {},
                onBack = { onDismiss() }
            )
        }
    ) { paddingValues ->
        Content(paddingValues, onSettingsEdited)
    }
}

@Composable
private fun Content(paddingValues: PaddingValues, onSettingsEdited: (DailyMoodSettings) -> Unit) {
    Column(
        modifier = Modifier.padding(paddingValues)
    ) {
        getDailyMoodSettings().forEach { (setting, icon) ->
            SettingItem(
                setting = setting,
                icon = icon,
                onSelected = {
                    when (setting) {
                        Settings.DEFAULT -> {}
                        Settings.TIMING -> {}
                        Settings.SHARE_PLATFORM -> {}
                        Settings.NOTIFY -> {}
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingItem(setting: Settings, icon: Int, onSelected: () -> Unit) {
    Row {
        SettingIcon(icon = icon)
        SettingTitle(title = setting.name)
    }
}

@Composable
fun SettingIcon(icon: Int) {
    Icon(
        painter = painterResource(icon),
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(28.dp)
    )
}

@Composable
fun SettingTitle(title: String) {
    Text(
        text = title,
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