package com.emc.moodmingle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient

var globalOnClick: (String) -> Unit = {}

@Composable
fun SettingsScreen(onBackClick: () -> Unit, onClick: (String) -> Unit) {
    globalOnClick = onClick

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = BrushPrimaryGradient),
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
                text = "Settings",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        MainContent()
    }
}

@Composable
fun MainContent() {
    val cards = listOf(
        Pair(
            "Personal Information",
            Pair("Personal" to R.drawable.personal, "Password" to R.drawable.password)
        ),
        Pair(
            "Privacy & Security",
            Pair("Privacy" to R.drawable.privacy, "Security" to R.drawable.security)
        ),
        Pair(
            "Saved & Favorite Posts",
            Pair("Saved" to R.drawable.save_post, "Favorites" to R.drawable.favorites)
        ),
        Pair(
            "Encryption & Logout",
            Pair("Encrypt" to R.drawable.encryption, "Logout" to R.drawable.logout)
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Spacer(Modifier.height(1.dp)) }

        items(cards) { (title, pair) ->
            ActionCards(
                pair = Pair(
                    pair.first.first to painterResource(id = pair.first.second),
                    pair.second.first to painterResource(id = pair.second.second)
                ),
                title = title
            )
        }
    }
}

@Composable
fun ActionCards(pair: Pair<Pair<String, Painter>, Pair<String, Painter>>, title: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )

        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionCard(pair.first)
            ActionCard(pair.second)
        }
    }
}

@Composable
fun ActionCard(pair: Pair<String, Painter>) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable {
                if (pair.first.lowercase() == "logout") {
                    showLogoutDialog = true
                }
            }
            .background(
                shape = RoundedCornerShape(16.dp),
                brush = BrushPrimaryGradient
            ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                modifier = Modifier.size(30.dp),
                painter = pair.second,
                contentDescription = "${pair.first} Icon",
                tint = Color.White
            )
            Text(text = pair.first, color = Color.White)
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    globalOnClick(pair.first)
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen(onBackClick = {}, onClick = {})
}