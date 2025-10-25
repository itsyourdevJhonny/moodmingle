package com.emc.moodmingle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PurplePrimary

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
    LazyColumn(
        modifier = Modifier
            .padding(bottom = 30.dp)
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            CreateTitle(
                text = "Choose your mood",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )
        }

        item { CreateMoods() }

        item {
            CreateTitle(
                text = "Hashtag",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
        }

        item { CreateHashTag() }

        item {
            CreateTitle(
                text = "What do you want to say?",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
        }

        item { CreateDescription() }

        item { CreatePostButton(onBackClick) }
    }
}

@Composable
fun CreateMoods() {
    val moods = listOf(
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

    var selectedMood by remember { mutableStateOf("") }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .height(400.dp)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                val topY = strokeWidth / 2
                val bottomY = size.height - strokeWidth / 2

                drawLine(
                    color = PurplePrimary,
                    start = Offset(0f, topY),
                    end = Offset(size.width, topY),
                    strokeWidth = strokeWidth
                )

                drawLine(
                    color = PurplePrimary,
                    start = Offset(0f, bottomY),
                    end = Offset(size.width, bottomY),
                    strokeWidth = strokeWidth
                )
            }
    ) {
        items(moods) { (emoji, text) ->
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .padding(4.dp)
                    .border(
                        width = if (selectedMood == emoji) 2.dp else 1.dp,
                        color = if (selectedMood == emoji) PurplePrimary else Color.Gray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { selectedMood = emoji },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = emoji, fontSize = 24.sp)
                    Text(text = text, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CreateTitle(text: String, modifier: Modifier) {
    Text(
        modifier = modifier,
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
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        label = { Text(text = "#") },
        value = text,
        singleLine = true,
        onValueChange = { text = it },
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
fun CreateDescription() {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        label = { Text(text = "Description") },
        value = text,
        onValueChange = { text = it },
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

@Preview(showBackground = true)
@Composable
fun PreviewCreatePostScreen() {
    CreatePostScreen {}
}