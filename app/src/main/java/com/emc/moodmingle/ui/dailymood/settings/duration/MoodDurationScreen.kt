package com.emc.moodmingle.ui.dailymood.settings.duration

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.MoodDuration
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.MoodDurationType
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.ScaffoldHeader

@Composable
fun MoodDurationScreen(
    settings: DailyMoodSettings,
    onEdit: (DailyMoodSettings) -> Unit,
    onBack: () -> Unit,
) {
    val duration = settings.duration

    var showCustomDuration by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Black,
        topBar = { ScaffoldHeader(title = "Mood Duration") { onBack() } }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Set how long your daily moods remain visible.",
                color = Color.White,
                style = Typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            MoodDurationType.entries.forEach { type ->
                DurationItem(
                    type = type,
                    selected = duration.type == type,
                    onSelected = { onEdit(settings.copy(duration = duration.copy(type = it))) },
                    onCustom = { showCustomDuration = true }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            DurationExplanation(duration)
        }
    }

    if (showCustomDuration) {
        CustomDurationDialog(
            duration,
            onCustomChanged = {
                onEdit(
                    settings.copy(
                        duration = settings.duration.copy(
                            type = MoodDurationType.CUSTOM,
                            customHours = it
                        )
                    )
                )
            },
            onDismiss = { showCustomDuration = false }
        )
    }
}

@Composable
private fun DurationItem(
    type: MoodDurationType,
    selected: Boolean,
    onSelected: (MoodDurationType) -> Unit,
    onCustom: () -> Unit,
) {
    val background = if (selected) SecondaryDark else PrimaryDark

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (type != MoodDurationType.CUSTOM) onSelected(type) else onCustom() },
        colors = CardDefaults.cardColors(containerColor = background)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = type.displayName(),
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    style = Typography.bodyLarge
                )

                RadioButton(
                    selected = selected,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.White,
                        unselectedColor = Color.White
                    )
                )
            }
        }
    }
}

private fun MoodDurationType.displayName(): String {
    return when (this) {
        MoodDurationType.FOREVER -> "Forever"
        MoodDurationType.HOURS_6 -> "6 Hours"
        MoodDurationType.HOURS_24 -> "24 Hours"
        MoodDurationType.DAYS_3 -> "3 Days"
        MoodDurationType.DAYS_7 -> "7 Days"
        MoodDurationType.CUSTOM -> "Custom Duration"
    }
}

@Composable
private fun DurationExplanation(duration: MoodDuration) {
    val text = when (duration.type) {
        MoodDurationType.FOREVER -> "Your mood will remain visible until you delete it."
        MoodDurationType.HOURS_6 -> "Your mood will disappear after 6 hours."
        MoodDurationType.HOURS_24 -> "Your mood will disappear after 24 hours."
        MoodDurationType.DAYS_3 -> "Your mood will disappear after 3 days."
        MoodDurationType.DAYS_7 -> "Your mood will disappear after 7 days."
        MoodDurationType.CUSTOM -> "Your mood will disappear after ${duration.customHours ?: 0} hours."
    }

    Text(
        text = text,
        color = GrayTextColor,
        style = Typography.bodySmall,
        modifier = Modifier.animateContentSize()
    )
}
