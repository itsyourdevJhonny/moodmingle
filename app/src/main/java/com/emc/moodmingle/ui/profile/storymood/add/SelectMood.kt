package com.emc.moodmingle.ui.profile.storymood.add

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.screens.getMoods
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.TertiaryDark

@Composable
fun SelectMood(onSelectedMood: (String) -> Unit) {
    var selectedMoodText by remember { mutableStateOf("") }
    var selectedMoodEmoji by remember { mutableStateOf("") }

    var showMoodsDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .background(SecondaryDark, CircleShape)
            .border(width = 0.5.dp, color = TertiaryDark, CircleShape)
            .clickable { showMoodsDialog = true }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (selectedMoodEmoji.isNotBlank() && selectedMoodText.isNotBlank()) {
                    Text(text = selectedMoodEmoji, color = Color.White)
                    Text(text = selectedMoodText, color = Color.White)
                } else {
                    Image(
                        painter = painterResource(R.drawable.feelings_colored),
                        contentDescription = "Feelings",
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Crop
                    )

                    Text(text = "Select Mood")
                }
            }
        }
    }

    if (showMoodsDialog) {
        DisplayMoodsDialog(
            selectedMoodText = selectedMoodText,
            onSelectedMood = onSelectedMood,
            onSelectedMoodText = { selectedMoodText = it },
            onSelectedMoodEmoji = { selectedMoodEmoji = it },
            onShowMoodsDialog = { showMoodsDialog = it }
        )
    }
}

@Composable
private fun DisplayMoodsDialog(
    selectedMoodText: String,
    onSelectedMood: (String) -> Unit,
    onSelectedMoodText: (String) -> Unit,
    onSelectedMoodEmoji: (String) -> Unit,
    onShowMoodsDialog: (Boolean) -> Unit,
) {
    val moods = getMoods()

    Dialog(
        onDismissRequest = { onShowMoodsDialog(false) },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            items(moods) { (emoji, text) ->
                val isSelected = text == selectedMoodText

                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .padding(4.dp)
                        .background(color = SecondaryDark, shape = RoundedCornerShape(8.dp))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) PurpleDark else PurplePrimary.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            if (isSelected) onSelectedMood("") else onSelectedMood(emoji)
                            onSelectedMoodText(if (isSelected) "" else text)
                            onSelectedMoodEmoji(if (isSelected) "" else emoji)
                            onShowMoodsDialog(false)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = emoji, fontSize = 24.sp)
                        Text(text = text, color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}