package com.emc.moodmingle.ui.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.theme.PrimaryGradient

@Composable
fun ProfileSection(@DrawableRes avatarId: Int, name: String, bio: String = "", joinedDate: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                Brush.linearGradient(colors = PrimaryGradient),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CreateAvatar(avatarId)

            Spacer(Modifier.height(8.dp))

            CreateName(name)

            Spacer(Modifier.height(8.dp))

            CreateBio(bio)

            Spacer(Modifier.height(8.dp))

            CreateJoinedDate(joinedDate)
        }
    }
}

@Composable
fun CreateAvatar(@DrawableRes avatarId: Int) {
    Image(
        painter = painterResource(avatarId),
        contentDescription = "Profile Picture",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.3f))
    )
    /*Box(
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 10.dp)
            .size(80.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Purple40, CalmBlue)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = avatar,
            fontSize = 34.sp,
            textAlign = TextAlign.Center
        )
    }*/
}

@Composable
fun CreateName(name: String) {
    Text(
        text = name,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun CreateBio(bio: String) {
    Text(
        text = bio,
        color = Color.White
    )
}

@Composable
fun CreateJoinedDate(joinedDate: String) {
    Text(text = joinedDate)
}