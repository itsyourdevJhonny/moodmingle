package com.emc.moodmingle.ui.dailymood.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
            ScaffoldHeader(title = "Story Settings") {
                if (originalSettings != mood.settings) toastMessage(context, "Settings Saved")
                onDismiss()
            }
        }
    ) { paddingValues ->
        Content(paddingValues)
    }
}

@Composable
private fun Content(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier.padding(paddingValues)
    ) {

    }
}