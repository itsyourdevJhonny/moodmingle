package com.emc.moodmingle.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.emc.moodmingle.ui.post.audio.FilePicker
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.ui.theme.SecondaryDark

@Composable
fun CreatePostScreen(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CreateTopBar(onBackClick)
        CreateMainContent(onBackClick)
    }
}

@Composable
fun CreateTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrushPrimaryGradient)
            .padding(top = 20.dp),
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "Back Icon",
                tint = Color.White
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            text = "Create Post",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun CreateMainContent(onBackClick: () -> Unit) {
    var caption by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .padding(bottom = 30.dp)
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { CreateTitle(text = "Choose your mood") }
        item { CreateMoods() }

        item { CreateTitle(text = "Hashtag") }
        item { CreateHashTag() }

        item { CreateTitle(text = "What will be the caption?") }
        item { CreateInputField(caption, "Caption", onValueChange = { caption = it }) }

        item { CreateTitle(text = "What do you want to say?") }
        item { CreateInputField(description, "Description", onValueChange = { description = it }) }

        item { CreateTitle(text = "Upload Image/Video/Audio") }
        item { FilePicker() }

        item { CreatePostButton(onBackClick) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMoods() {
    var selectedMood by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(top = 10.dp)
            .border(1.dp, if (selected) PurplePrimary else Color.Gray, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { showDialog = true }
            ) {
                Text(
                    text = selectedMood,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        Color.White
                    )
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { showDialog = true },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    modifier = Modifier
                        .graphicsLayer(alpha = 0.99f)
                        .drawWithCache{
                            onDrawWithContent {
                                drawContent()
                                drawRect(
                                    brush = BrushPrimaryGradient,
                                    blendMode = BlendMode.SrcAtop
                                )
                            }
                        },
                    contentDescription = "Select",
                )

                Text(
                    text = "Select",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                )
            }
        }
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            content = {
                DisplayMoods(
                    onSelectedMood = { selectedMood = it },
                    onSelected = { selected = it },
                    onShowDialog = { showDialog = it }
                )
            }
        )
    }
}

@Composable
fun DisplayMoods(
    onSelectedMood: (String) -> Unit,
    onSelected: (Boolean) -> Unit,
    onShowDialog: (Boolean) -> Unit
) {
    val moods = getMoods()

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        items(moods) { (emoji, text) ->
            val isSelected = emoji == selectedMood

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
                        color = if (isSelected) PurpleDark else PurplePrimary.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        if (isSelected) onSelectedMood("") else onSelectedMood(emoji)
                        onSelected(!isSelected)
                        onShowDialog(false)
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

@Composable
fun CreateTitle(text: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    )
}

@Composable
fun CreateHashTag() {
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        label = { Text(text = "#") },
        value = textFieldValue,
        singleLine = true,
        onValueChange = { newText ->
            textFieldValue = if (newText.text.startsWith("#")) {
                newText.copy(selection = TextRange(newText.text.length))
            } else {
                TextFieldValue(
                    text = "#${newText.text}",
                    selection = TextRange(newText.text.length + 1)
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedLabelColor = GrayTextColor,
            focusedLabelColor = Color.White,
            focusedBorderColor = PurplePrimary,
            focusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(30.dp)
    )
}

@Composable
fun CreateInputField(text: String, label: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        label = { Text(text = label) },
        value = text,
        onValueChange = onValueChange,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedLabelColor = GrayTextColor,
            focusedLabelColor = Color.White,
            focusedBorderColor = PurplePrimary,
            focusedTextColor = Color.White
        )
    )
}

@Composable
fun CreatePostButton(onBackClick: () -> Unit) {
    Button(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 20.dp)
            .fillMaxWidth()
            .background(
                brush = BrushPrimaryGradient,
                shape = RoundedCornerShape(30.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        onClick = onBackClick
    ) {
        Text(
            text = "Create Post",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

fun getMoods(): List<Pair<String, String>> {
    return listOf(
        "😀" to "Happy",
        "😌" to "Calm",
        "🤗" to "Excited",
        "🙏" to "Grateful",
        "😬" to "Anxious",
        "😢" to "Sad",
        "😠" to "Angry",
        "😴" to "Sleepy",
        "🤔" to "Thoughtful",
        "😳" to "Embarrassed",
        "😇" to "Content",
        "🤩" to "Amazed",
        "🥰" to "Loved",
        "😭" to "Heartbroken",
        "😎" to "Confident",
        "😕" to "Confused",
        "😮" to "Surprised",
        "😒" to "Bored",
        "😤" to "Frustrated",
        "🤒" to "Sick",
        "🤪" to "Playful",
        "😞" to "Disappointed",
        "🥳" to "Cheerful",
        "🤯" to "Overwhelmed",
        "🥺" to "Hopeful",
        "😔" to "Lonely",
        "😱" to "Scared",
        "🤫" to "Secretive",
        "😐" to "Neutral",
        "🫠" to "Exhausted"
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewCreatePostScreen() {
    CreatePostScreen {}
}