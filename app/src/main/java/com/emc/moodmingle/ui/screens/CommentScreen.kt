package com.emc.moodmingle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient

data class Comment(
    val name: String,
    val avatar: String,
    val comment: String,
    val timeAgo: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen() {
    val randomComments = listOf(
        "That looks awesome!",
        "I totally agree with you.",
        "Wow, I didn’t expect that!",
        "Nice work!",
        "This made my day 😊",
        "Can you tell me more about it?",
        "So cool! 🔥",
        "That’s interesting, I never thought of that.",
        "I really love this idea!",
        "This is super helpful, thanks!",
        "Hahaha this made me laugh 😂",
        "Looks great!",
        "I’ve been waiting for this!",
        "Absolutely stunning!",
        "I feel the same way.",
        "Such a nice post!",
        "Love this vibe!",
        "Thanks for sharing!",
        "This is gold!",
        "Can’t stop looking at this!"
    ).shuffled()

    val dummyComments = List(20) { index ->
        Comment(
            name = "User ${index + 1}",
            avatar = listOf("😀", "😎", "😊", "😇", "🥳", "🤩").random(),
            comment = randomComments[index],
            timeAgo = "${(1..5).random()}h ago"
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .height(500.dp)
    ) {
        items(dummyComments) { comment ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(BrushPrimaryGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = comment.avatar,
                        fontSize = 26.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Column {
                    Text(
                        text = comment.name,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = comment.comment,
                        color = Color.LightGray
                    )
                    Text(
                        text = comment.timeAgo,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCommentScreen() {
    CommentScreen()
}
