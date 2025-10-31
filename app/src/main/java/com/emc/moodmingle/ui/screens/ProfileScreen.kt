package com.emc.moodmingle.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.profile.ComingSoonSection
import com.emc.moodmingle.ui.profile.ProfileSection
import com.emc.moodmingle.ui.profile.QuickActionsSection
import com.emc.moodmingle.ui.profile.getPosts

@Composable
fun ProfileScreen(
    @DrawableRes avatarId: Int,
    username: String,
    bio: String = "",
    joinedDate: String,
    onCreateClick: () -> Unit = {},
    onInsightsClick: () -> Unit = {},
    onExploreClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .padding(top = 20.dp)
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { ProfileSection(avatarId, username, bio, joinedDate) }

            item {
                QuickActionsSection(
                    onCreateClick = onCreateClick,
                    onExploreClick = onExploreClick,
                    onInsightsClick = onInsightsClick,
                    onLogoutClick = onLogoutClick
                )
            }

            item {
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = "Posts",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            items(getPosts(avatarId, "😄", username)) { post ->
                MoodPostCard(post)
            }

            item { ComingSoonSection() }
        }
    }
}