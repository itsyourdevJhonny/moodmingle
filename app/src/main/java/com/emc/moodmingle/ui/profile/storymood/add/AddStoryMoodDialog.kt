package com.emc.moodmingle.ui.profile.storymood.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography

@Composable
fun AddStoryMoodDialog(onDismiss: () -> Unit) {
    var selectedMood by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryDark)
                .padding(start = 16.dp, end = 16.dp, bottom = 42.dp)
        ) {
            DialogHeader(onDismiss, selectedMood, description)
            DialogContent(
                onSelectedMood = { selectedMood = it },
                onDescription = { description = it }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogHeader(onDismiss: () -> Unit, selectedMood: String, description: String) {
    var showDiscardDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier.clickable {
                if (description.isNotBlank() || selectedMood.isNotBlank()) {
                    showDiscardDialog = true
                    return@clickable
                }

                onDismiss()
            }
        )

        Text(
            text = "Add Story Mood",
            style = Typography.bodyLarge.copy(color = Color.White, fontWeight = FontWeight.Black)
        )

        Box(
            modifier = Modifier
                .background(BrushPrimaryGradient, CircleShape)
                .clickable { onDismiss() }
        ) {
            Text(
                text = "Save",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }

    if (showDiscardDialog) {
        DiscardDialog(onDismiss, onShowDiscardDialog = { showDiscardDialog = it })
    }
}

@Composable
private fun DiscardDialog(onDismiss: () -> Unit, onShowDiscardDialog: (Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.background(BrushPrimaryGradient, CircleShape)
                ) {
                    Text(text = "Confirm", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onShowDiscardDialog(false) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.background(SecondaryDark, CircleShape)
                ) {
                    Text(text = "Cancel", fontWeight = FontWeight.Bold)
                }
            }
        },
        title = {
            Text(
                text = "Discard draft?",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = "Are you sure you want to discard this story mood draft?",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = PrimaryDark,
        shape = RectangleShape,
        modifier = Modifier.border(
            width = 0.5.dp,
            brush = BrushPrimaryGradient,
            shape = RectangleShape
        )
    )
}

@Composable
private fun DialogContent(onSelectedMood: (String) -> Unit, onDescription: (String) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End
    ) {
        StoryMoodPrivacy()
        SelectMood(onSelectedMood)
        StoryMoodDescription(onDescription)
        StoryMoodMedia()
    }
}