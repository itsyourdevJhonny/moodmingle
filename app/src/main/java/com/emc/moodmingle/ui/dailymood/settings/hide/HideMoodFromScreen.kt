package com.emc.moodmingle.ui.dailymood.settings.hide

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.ScaffoldHeader
import com.emc.moodmingle.utils.components.UserSelectorDialog
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@Composable
fun HideMoodFromScreen(
    settings: DailyMoodSettings,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
    onDismiss: () -> Unit,
) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val hiddenUserIds = settings.hiddenUserIds

    var showSelector by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Black,
        topBar = { ScaffoldHeader(title = "Hide Mood from People") { onDismiss() } }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            HeaderText()

            Spacer(modifier = Modifier.height(8.dp))

            SubHeaderText()

            Spacer(modifier = Modifier.height(24.dp))

            // Selector row
            Selector(settings) { showSelector = true }

            HorizontalDivider(thickness = 0.5.dp)

            if (hiddenUserIds.isNotEmpty()) {
                HiddenUsersAvatar(hiddenUserIds, userViewModel)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (settings.hiddenUserIds.isNotEmpty()) {
                SupportingText()
            }
        }
    }

    if (showSelector) {
        UserSelectorDialog(
            headerLabel = "Hide mood from",
            userIds = settings.hiddenUserIds,
            onUsersSelected = { selected ->
                val selectedUserIds = (selected as SnapshotStateList<*>).map { it.toString() }
                onSettingsEdited(settings.copy(hiddenUserIds = selectedUserIds))
                showSelector = false
            },
            onDismiss = { showSelector = false }
        )
    }
}

@Composable
private fun SubHeaderText() {
    Text(
        text = "They won’t be notified that they are hidden.",
        color = GrayTextColor,
        style = Typography.bodySmall
    )
}

@Composable
private fun HeaderText() {
    Text(
        text = "Select people who won’t be able to see your daily moods.",
        color = Color.White,
        style = Typography.bodyMedium
    )
}

@Composable
private fun Selector(settings: DailyMoodSettings, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = if (settings.hiddenUserIds.isEmpty()) "No people selected" else "${settings.hiddenUserIds.size} people hidden",
            color = Color.White,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
private fun HiddenUsersAvatar(hiddenUserIds: List<String>, userViewModel: FirebaseUserViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        (if (hiddenUserIds.size > 8) hiddenUserIds.take(8) else hiddenUserIds).forEach { userId ->
            val user =
                userViewModel.getUserById(userId).collectAsState(initial = null).value

            AsyncImage(
                model = user?.avatarUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .gradientCircleBorder()
            )
        }

        if (hiddenUserIds.size > 8) {
            Text(
                text = "+ ${hiddenUserIds.size - 8}",
                color = GrayTextColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SupportingText() {
    Text(
        text = "You can manage or remove people anytime.",
        color = GrayTextColor,
        style = Typography.bodySmall,
        modifier = Modifier.animateContentSize()
    )
}
