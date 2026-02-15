package com.emc.moodmingle.ui.dailymood.settings.timing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.SettingsTiming
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyMoodScheduleDialog(
    timing: SettingsTiming,
    onScheduleCreated: (LocalDate?) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PrimaryDark,
        iconContentColor = Color.White,
        icon = { ScheduleAndCloseIcon(onDismiss) },
        titleContentColor = Color.White,
        title = { Title() },
        text = { ScheduleContent(timing, onScheduleCreated, onDismiss) },
        confirmButton = {}
    )
}

@Composable
private fun ScheduleAndCloseIcon(onDismiss: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Icon(
            painter = painterResource(R.drawable.schedule),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .drawGradient()
        )

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .offset(y = (-20).dp, x = 18.dp)
                .align(Alignment.TopEnd)
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color.Red)
        }
    }
}

@Composable
private fun Title() {
    Text(text = "Choose Schedule")
}

@Composable
private fun ScheduleContent(
    timing: SettingsTiming,
    onScheduleCreated: (LocalDate?) -> Unit,
    onDismiss: () -> Unit,
) {
    Column(modifier = Modifier) {
        (0L..7L).forEach { date -> ScheduleItem(date, timing, onScheduleCreated, onDismiss) }
    }
}

@Composable
private fun ScheduleItem(
    date: Long,
    timing: SettingsTiming,
    onScheduleCreated: (LocalDate?) -> Unit,
    onDismiss: () -> Unit,
) {
    val calculatedDate = LocalDate.now().plusDays(date)
    val isSelected = calculatedDate == timing.date || (date == 0L && timing.date == null)

    TextButton(
        onClick = {
            onScheduleCreated(if (date == 0L) null else calculatedDate)
            onDismiss()
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.textButtonColors(containerColor = if (isSelected) SecondaryDark else Color.Transparent)
    ) {
        Icon(
            painter = painterResource(if (date == 0L) R.drawable.none_image else R.drawable.time),
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .drawGradient()
        )

        Spacer(Modifier.width(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (date) {
                    0L -> "None"
                    1L -> "Tomorrow"
                    else -> "$date days from now"
                },
                color = if (isSelected) Color.White else GrayTextColor,
                style = if (isSelected) Typography.titleMedium else Typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
            )
        }
    }
}