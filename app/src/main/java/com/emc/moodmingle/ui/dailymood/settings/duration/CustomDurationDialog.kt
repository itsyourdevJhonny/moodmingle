package com.emc.moodmingle.ui.dailymood.settings.duration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.domain.remote.model.post.dailymood.settings.MoodDuration
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography

@Composable
fun CustomDurationDialog(
    duration: MoodDuration,
    onCustomChanged: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var customDuration by remember { mutableIntStateOf(duration.customHours ?: 0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { DialogHeader(onDismiss) },
        confirmButton = { DialogConfirmButton(customDuration, onCustomChanged, onDismiss) },
        text = { DialogContent(customDuration) { customDuration = it } },
        containerColor = PrimaryDark
    )
}

@Composable
private fun DialogContent(customDuration: Int, onDurationChanged: (Int) -> Unit) {
    OutlinedTextField(
        value = if (customDuration == 0) "" else customDuration.toString(),
        onValueChange = { onDurationChanged(it.toInt()) },
        label = { Text(text = "Hours") },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedTextColor = Color.White,
            unfocusedBorderColor = Color.White,
            focusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            focusedLabelColor = Color.White,
            cursorColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun DialogConfirmButton(
    customDuration: Int,
    onCustomChanged: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    TextButton(
        onClick = { onCustomChanged(customDuration); onDismiss() },
        colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
        content = { Text(text = "Confirm") }
    )
}

@Composable
private fun DialogHeader(onDismiss: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HeaderTitle()
        HeaderDismissIcon(onDismiss)
    }
}

@Composable
private fun RowScope.HeaderTitle() {
    Text(
        text = "Custom Duration",
        style = Typography.bodyLarge.copy(color = Color.White),
        modifier = Modifier.weight(1f)
    )
}

@Composable
private fun HeaderDismissIcon(onDismiss: () -> Unit) {
    IconButton(onClick = onDismiss) {
        Icon(imageVector = Icons.Default.Close, tint = Color.Red, contentDescription = null)
    }
}