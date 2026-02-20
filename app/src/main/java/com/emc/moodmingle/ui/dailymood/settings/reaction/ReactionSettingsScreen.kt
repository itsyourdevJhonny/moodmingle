package com.emc.moodmingle.ui.dailymood.settings.reaction

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.ScaffoldHeader

@Composable
fun ReactionSettingsScreen(
    settings: DailyMoodSettings,
    onEdit: (DailyMoodSettings) -> Unit,
    onBack: () -> Unit,
) {
    val enabled = settings.reactionEnabled

    Scaffold(
        containerColor = Color.Black,
        topBar = { ScaffoldHeader(title = "Reaction Settings") { onBack() } }
    ) { paddingValues ->
        Content(paddingValues, enabled, onEdit, settings)
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    enabled: Boolean,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
    settings: DailyMoodSettings,
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        LoveReactionCard(
            enabled = enabled,
            onToggle = { onSettingsEdited(settings.copy(reactionEnabled = it)) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Description(enabled)
    }
}

@Composable
private fun Description(enabled: Boolean) {
    Text(
        text = if (enabled) "People can send ❤️ to your moods." else "Reactions are disabled for your moods.",
        color = GrayTextColor,
        style = Typography.bodySmall
    )
}

@Composable
private fun LoveReactionCard(enabled: Boolean, onToggle: (Boolean) -> Unit) {
    val background = if (enabled) Color(0xFF1C1C1E) else Color(0xFF121212)
    val borderColor = if (enabled) Color(0xFFFF4D6D) else Color.Gray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!enabled) },
        colors = CardDefaults.cardColors(containerColor = background),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.love),
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Love Reactions", color = Color.White, style = Typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (enabled) "Tap to disable" else "Tap to enable",
                color = GrayTextColor,
                style = Typography.bodySmall
            )

            Spacer(modifier = Modifier.height(16.dp))
            ToggleButton(enabled, onToggle)
        }
    }
}

@Composable
private fun ToggleButton(enabled: Boolean, onToggle: (Boolean) -> Unit) {
    Switch(
        checked = enabled,
        onCheckedChange = onToggle,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.Red,
            uncheckedThumbColor = Color.White,
            checkedTrackColor = Color(0xFFFF4D6D),
            uncheckedTrackColor = Color.Transparent,
            uncheckedBorderColor = Color.White
        )
    )
}

