package com.emc.moodmingle.ui.remix

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.remix.Mood
import com.emc.moodmingle.ui.screens.getMoods
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.TertiaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.BackIcon
import com.emc.moodmingle.utils.modifier.grayCircleBorder
import com.emc.moodmingle.utils.modifier.roundedGrayBorder

@Composable
fun RemixInformationActions(
    isHidden: Boolean,
    hashtag: String,
    caption: String,
    description: String,
    selectedMood: Mood,
    onHidden: (Boolean) -> Unit,
    onHashtagChange: (String) -> Unit,
    onCaptionChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSelectedMood: (Mood) -> Unit
) {
    var isSelected by remember { mutableStateOf(false) }
    var selectedAction by remember { mutableStateOf("") }

    var showMood by remember { mutableStateOf(false) }

    listOf(
        "Mood" to R.drawable.mood,
        "Hashtag" to R.drawable.hashtag,
        "Caption" to R.drawable.caption,
        "Description" to R.drawable.description,
        "Hide" to R.drawable.hidden
    ).forEach { (text, icon) ->
        AnimatedVisibility(
            visible = !isHidden,
            enter = expandVertically(initialHeight = { maxHeight -> maxHeight / 100 }),
            exit = fadeOut()
        ) {
            val selected = selectedAction == text
            val isHide = text == "Hide"

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                InformationInputField(
                    isSelected,
                    selected,
                    selectedAction,
                    text,
                    hashtag,
                    caption,
                    description,
                    onHashtagChange,
                    onCaptionChange,
                    onDescriptionChange
                )

                InformationIcon(
                    icon,
                    isHide,
                    selectedAction,
                    text,
                    isSelected,
                    onHidden,
                    onSelected = { isSelected = it },
                    onSelectedAction = { selectedAction = it },
                    onShowMood = { showMood = it }
                )
            }
        }
    }

    if (showMood) {
        MoodPickerDialog(
            selectedMood,
            onDismiss = { showMood = false },
            onSelectedMood
        )
    }
}

@Composable
fun MoodPickerDialog(
    selectedMood: Mood,
    onDismiss: () -> Unit,
    onSelectedMood: (Mood) -> Unit
) {
    val moods = getMoods()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryDark)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                BackIcon(onClick = onDismiss)
                Text(
                    text = "Select Mood",
                    style = Typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Box {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    items(moods) { (emoji, description) ->
                        val isSelected = emoji == selectedMood.emoji

                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .padding(4.dp)
                                .background(
                                    color = SecondaryDark,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) PurpleDark else TertiaryDark,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    onSelectedMood(Mood(emoji, description))
                                    onDismiss()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.padding(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = emoji, fontSize = 24.sp)
                                Text(text = description, color = Color.White, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.InformationInputField(
    isSelected: Boolean,
    selected: Boolean,
    selectedAction: String,
    text: String,
    hashtag: String,
    caption: String,
    description: String,
    onHashtagChange: (String) -> Unit,
    onCaptionChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    AnimatedVisibility(
        visible = isSelected && selected,
        enter = expandHorizontally(initialWidth = { maxWidth -> maxWidth / 100 }),
        exit = fadeOut()
    ) {
        val isNotDescription = listOf("Hashtag", "Caption").contains(selectedAction)

        TextField(
            value = when (text) {
                "Hashtag" -> hashtag
                "Caption" -> caption
                else -> description
            },
            onValueChange = {
                when (text) {
                    "Hashtag" -> onHashtagChange(it)
                    "Caption" -> onCaptionChange(it)
                    else -> onDescriptionChange(it)
                }
            },
            placeholder = { Text(text = "Enter ${text.lowercase()}...") },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                focusedContainerColor = Color.Transparent
            ),
            shape = if (isNotDescription) CircleShape else RoundedCornerShape(8.dp),
            singleLine = isNotDescription,
            modifier = Modifier
                .width(270.dp)
                .background(
                    SecondaryDark,
                    if (isNotDescription) CircleShape else RoundedCornerShape(8.dp)
                )
                .heightIn(max = if (isNotDescription) 100.dp else 110.dp)
                .roundedGrayBorder(if (isNotDescription) Int.MAX_VALUE.dp else 8.dp)
                .animateContentSize()
        )
    }
}

@Composable
private fun InformationIcon(
    icon: Int,
    isHide: Boolean,
    selectedAction: String,
    text: String,
    isSelected: Boolean,
    onHidden: (Boolean) -> Unit,
    onSelected: (Boolean) -> Unit,
    onSelectedAction: (String) -> Unit,
    onShowMood: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            .grayCircleBorder()
            .clickable {
                if (text == "Mood") {
                    onShowMood(true)
                } else {
                    if (isHide) {
                        onHidden(true)
                    } else {
                        onSelected(!(selectedAction == text && isSelected))
                        onSelectedAction(text)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "Action",
            tint = Color.White,
            modifier = Modifier.size(if (isHide) 20.dp else 26.dp)
        )
    }
}