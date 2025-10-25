package com.emc.moodmingle.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.ui.theme.SecondaryDark

@Composable
fun ComingSoonSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                color = SecondaryDark,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        CreateComingSoonTitle()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            CreateComingSoonContent(
                Icons.Default.Star,
                "Mood Playlists",
                "Curated music for your moods"
            )
            CreateComingSoonContent(
                Icons.Default.Star,
                "Mood Groups",
                "Connect with similar feelings"
            )
            CreateComingSoonContent(
                Icons.Default.Star,
                "Advanced Analytics",
                "Deeper mood insights"
            )
        }
    }
}

@Composable
fun CreateComingSoonTitle() {
    Text(
        modifier = Modifier.padding(top = 20.dp, start = 20.dp),
        text = "Coming Soon",
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun CreateComingSoonContent(icon: ImageVector, title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(colors = listOf(PurplePrimary, PurpleDark)),
                shape = RoundedCornerShape(16.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(Modifier.height(15.dp))
        Icon(icon, contentDescription = "$title Icon", tint = Color.White)
        Text(text = title, color = Color.White, fontWeight = FontWeight.Bold)
        Text(text = description, color = GrayTextColor)
        Spacer(Modifier.height(15.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewComingSoonSection() {
    ComingSoonSection()
}