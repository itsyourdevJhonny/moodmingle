package com.emc.moodmingle.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    name: String,
    password: String,
    selectedAvatar: String,
    bio: String,
    onCreateClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopNavigationBar(selectedAvatar, onSearchClick)
        MoodFeedScreen(onCreateClick)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTopNavigationBar() {
}

@Composable
fun TopNavigationBar(selectedAvatar: String, onSearchClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.profile),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(top = 15.dp, bottom = 15.dp, start = 20.dp)
                .size(45.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.3f))
        )
        /*Box(
            modifier = Modifier
                .padding(top = 15.dp, bottom = 15.dp, start = 20.dp)
                .size(45.dp)
                .clip(CircleShape)
                .background(BrushPrimaryGradient),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = selectedAvatar,
                fontSize = 26.sp,
                textAlign = TextAlign.Center
            )
        }*/

        Text(
            text = "MoodMingle",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
                )
            )
        )

        Box {
            IconButton(
                modifier = Modifier.padding(end = 10.dp),
                onClick = onSearchClick
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
        }
    }
}