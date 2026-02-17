package com.emc.moodmingle.ui.dailymood.settings.viewlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodSettings
import com.emc.moodmingle.utils.components.ScaffoldHeader
import com.emc.moodmingle.utils.components.SwitchButton

@Composable
fun ViewListVisibility(
    settings: DailyMoodSettings,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
    onDismiss: () -> Unit,
) {
    Scaffold(
        topBar = { ScaffoldHeader(title = "View List Visibility") { onDismiss() } }
    ) { paddingValues ->
        Content(paddingValues, settings, onSettingsEdited)
    }
}

@Composable
fun Content(
    paddingValues: PaddingValues,
    settings: DailyMoodSettings,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
) {
    Column(
        modifier = Modifier.padding(paddingValues)
    ) {
        SwitchButton(
            label = "View List Visibility",
            isChecked = settings.viewListEnabled,
            onCheckedChange = { onSettingsEdited(settings.copy(viewListEnabled = it)) }
        )
    }
}