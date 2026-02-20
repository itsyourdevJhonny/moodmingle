package com.emc.moodmingle.ui.create.post.settings.audience

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.create.post.CreatePostDialogHeader
import com.emc.moodmingle.ui.post.action.toastMessage
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel

@Composable
fun AudienceDialog(audience: Any, onAudienceSelected: (Any) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val visibilityTypes = getAudienceTypes()
    val localCopy = remember(Unit) { audience }

    var showCustomDialog by remember { mutableStateOf(false) }

    Box {
        Scaffold(
            containerColor = Color.Black,
            topBar = {
                CreatePostDialogHeader(
                    label = "Update Audience",
                    onBack = {
                        if (audience != localCopy) toastMessage(context, "Settings Saved")
                        onDismiss()
                    }
                )
            },
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                visibilityTypes.forEach { visibilityType ->
                    val isChecked =
                        (if (audience is SnapshotStateList<*>) "Custom" else audience as String) == visibilityType.label

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (visibilityType.label == "Custom") showCustomDialog = true
                                else onAudienceSelected(visibilityType.label)
                            }
                            .padding(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IndicatorIcon(visibilityType.iconRes, visibilityType.label)
                            LabelAndDescription(visibilityType, audience)
                        }

                        RadioButton(
                            selected = isChecked,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                unselectedColor = Color.White,
                                selectedColor = PurplePrimary
                            )
                        )
                    }
                }
            }
        }

        if (showCustomDialog) {
            CustomAudienceDialog(
                headerLabel = "Custom Audience",
                audience,
                onAudienceSelected,
                onDismiss = { showCustomDialog = false }
            )
        }
    }
}

@Composable
private fun LabelAndDescription(visibilityType: Audience, audience: Any) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()

    Column {
        Text(text = visibilityType.label, color = Color.White, fontWeight = FontWeight.Bold)

        if (visibilityType.label != "Custom") {
            Text(
                text = visibilityType.description,
                style = Typography.bodySmall.copy(color = GrayTextColor)
            )
        } else {
            val customAudienceList =
                if (audience is SnapshotStateList<*>) audience.toList() else emptyList()

            val usernames = customAudienceList.mapNotNull {
                userViewModel.getUserById(it as String)
                    .collectAsState(initial = null).value?.username
            }

            val visibleUsers = usernames.take(3)
            val remainingCount = usernames.size - visibleUsers.size

            var joinedUsernames = visibleUsers.joinToString(", ")

            if (remainingCount > 0) {
                val othersLabel = if (remainingCount == 1) "1 other" else "$remainingCount others"
                joinedUsernames = "$joinedUsernames and $othersLabel"
            }

            Text(
                text = if (customAudienceList.isNotEmpty()) joinedUsernames else visibilityType.description,
                style = Typography.bodyMedium.copy(color = GrayTextColor),
                modifier = Modifier.widthIn(max = 256.dp)
            )
        }
    }
}

@Composable
private fun IndicatorIcon(icon: Int, label: String) {
    Box(
        modifier = Modifier
            .background(PrimaryDark, CircleShape)
            .padding(12.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

private fun getAudienceTypes(): List<Audience> {
    return listOf(
        Audience("Public", "Visible to everyone", R.drawable.public_world),
        Audience("Private", "Visible only to you", R.drawable.private_user),
        Audience("Followers", "Visible to your followers", R.drawable.followers),
        Audience("Supporters", "Visible to your supporters", R.drawable.supporter),
        Audience("Custom", "Choose who can view your post", R.drawable.custom)
    )
}

private data class Audience(
    val label: String,
    val description: String,
    @DrawableRes val iconRes: Int
)