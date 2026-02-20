package com.emc.moodmingle.ui.dailymood.settings.hide

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.ui.settings.saved.utils.EmptyComponent
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.utils.text.NumberFormatter
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@Composable
fun HideMoodFromPeopleScreen(
    settings: DailyMoodSettings,
    onSelectorOpen: () -> Unit,
    onEdit: (DailyMoodSettings) -> Unit,
) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val hiddenUserIds = settings.hiddenUserIds

    TitleAndSubtitle()

    Spacer(modifier = Modifier.height(16.dp))

    // Selector row
    Selector(settings, onSelectorOpen)

    HorizontalDivider(thickness = 0.5.dp)

    if (hiddenUserIds.isNotEmpty()) {
        HiddenPeopleCounterAndUnhideButton(hiddenUserIds, onEdit)
        HiddenUsersAvatar(hiddenUserIds, userViewModel)
    } else {
        EmptyComponent(iconRes = R.drawable.hidden, text = "No hidden people yet.")
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (settings.hiddenUserIds.isNotEmpty()) Hint()
}

@Composable
private fun HiddenPeopleCounterAndUnhideButton(
    hiddenUserIds: List<String>,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = NumberFormatter.formatValue(hiddenUserIds.size.toLong(), false),
                color = Color.White,
                style = Typography.bodyMedium
            )

            Text(text = "People Hidden", style = Typography.bodySmall.copy(color = GrayTextColor))
        }

        AnimatedVisibility(visible = hiddenUserIds.size > 1) {
            TextButton(
                onClick = { onSettingsEdited(DailyMoodSettings(hiddenUserIds = emptyList())) },
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) {
                Icon(
                    painter = painterResource(R.drawable.view),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(text = "Unhide All", style = Typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun TitleAndSubtitle() {
    Text(
        text = "Select people who won’t be able to see your daily moods.",
        color = Color.White,
        style = Typography.bodyMedium
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "They won’t be notified that they are hidden.",
        color = GrayTextColor,
        style = Typography.bodySmall
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
            text = if (settings.hiddenUserIds.isEmpty()) "No people selected" else "Select more people",
            color = Color.White,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.drawGradient()
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
private fun Hint() {
    Text(
        text = "You can manage or remove people anytime.",
        color = GrayTextColor,
        style = Typography.bodySmall,
        modifier = Modifier.animateContentSize()
    )
}
