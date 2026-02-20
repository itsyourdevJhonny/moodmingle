package com.emc.moodmingle.ui.dailymood.settings.replay

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.ReplayLimitType
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography

@Composable
fun ReplayLimitScreen(settings: DailyMoodSettings, onEdit: (DailyMoodSettings) -> Unit) {
    val isUnlimited = settings.replay.type == ReplayLimitType.UNLIMITED
    val isOnce = settings.replay.type == ReplayLimitType.ONCE
    val isCustom = !isUnlimited && !isOnce

    Text(
        text = "Control how many times someone can replay your daily mood.",
        color = Color.White,
        style = MaterialTheme.typography.bodyMedium
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Unlimited option
    SelectionItem(
        title = "Unlimited",
        selected = isUnlimited,
        onClick = { onEdit(settings.copy(replay = settings.replay.copy(type = ReplayLimitType.UNLIMITED))) }
    )

    // Allow once
    SelectionItem(
        title = "Allow once",
        selected = isOnce,
        onClick = { onEdit(settings.copy(replay = settings.replay.copy(type = ReplayLimitType.ONCE))) }
    )

    // Custom option
    SelectionItem(
        title = "Custom limit",
        selected = isCustom,
        onClick = { if (!isCustom) onEdit(settings.copy(replay = settings.replay.copy(type = ReplayLimitType.CUSTOM))) }
    )

    // Custom number selector
    if (isCustom) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Replay limit: ", style = Typography.bodyMedium, color = GrayTextColor)
            Text(
                text = "${settings.replay.customLimit}",
                style = Typography.bodyMedium,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = settings.replay.customLimit.toFloat(),
            onValueChange = { onEdit(settings.copy(replay = settings.replay.copy(customLimit = it.toInt()))) },
            valueRange = 1f..20f,
            steps = 18,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                activeTickColor = SecondaryDark,
                inactiveTickColor = Color.White,
                inactiveTrackColor = SecondaryDark
            )
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = when {
            isUnlimited -> "People can replay your mood without limits."
            isOnce -> "People can replay your mood only once."
            else -> "People can replay your mood up to ${settings.replay.customLimit} times."
        },
        color = GrayTextColor,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.animateContentSize()
    )
}

@Composable
private fun SelectionItem(title: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(text = title, color = Color.White, modifier = Modifier.weight(1f))

        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color.White,
                unselectedColor = Color.White
            )
        )
    }
}

