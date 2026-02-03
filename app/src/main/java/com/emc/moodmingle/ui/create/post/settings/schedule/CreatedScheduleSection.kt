package com.emc.moodmingle.ui.create.post.settings.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.data.firebase.model.post.settings.PostSchedule
import com.emc.moodmingle.ui.post.action.toastMessage
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.MentionTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.SuccessIcon
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatedScheduleSection(
    selectedDate: LocalDate?,
    selectedTime: Long,
    onScheduleCreated: (PostSchedule) -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        SuccessIcon()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 24.dp)
        ) {
            Title()
            Subtitle()
            SchedulePreview(dateFormatter, selectedDate, timeFormatter, selectedTime)
        }

        OkayButton(selectedDate, onScheduleCreated, selectedTime, onDismiss)
    }
}

@Composable
private fun Title() {
    Text(
        text = "Post Expiration Scheduled",
        style = Typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
    )
}

@Composable
private fun Subtitle() {
    Text(text = "The post will expire on:", color = GrayTextColor)
}

@Composable
private fun SchedulePreview(
    dateFormatter: DateTimeFormatter,
    selectedDate: LocalDate?,
    timeFormatter: SimpleDateFormat,
    selectedTime: Long
) {
    Box(
        modifier = Modifier
            .background(MentionTextColor, CircleShape)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        val formattedDate = dateFormatter.format(selectedDate)
        val formattedTime = timeFormatter.format(selectedTime)
        val dateTime = "$formattedDate at $formattedTime"

        Text(text = dateTime, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun OkayButton(
    selectedDate: LocalDate?,
    onScheduleCreated: (PostSchedule) -> Unit,
    selectedTime: Long,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    TextButton(
        onClick = {
            selectedDate?.let {
                onScheduleCreated(
                    PostSchedule(
                        expirationDate = selectedDate,
                        expirationTime = selectedTime
                    )
                )

                toastMessage(context, "Settings Saved")

                onDismiss()
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = SecondaryDark,
            contentColor = Color.White
        )
    ) {
        Icon(imageVector = Icons.Default.Check, contentDescription = "Check")
        Text(text = " Okay", fontWeight = FontWeight.Bold)
    }
}