package com.emc.moodmingle.ui.dailymood.visibility

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.AudienceType
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.ScaffoldHeader
import com.emc.moodmingle.utils.components.UserSelectorDialog
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@Composable
fun DailyMoodAudience(
    mood: DailyMoodEntity,
    onEdited: (DailyMoodEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    Scaffold(
        containerColor = Color.Black,
        topBar = { ScaffoldHeader(title = "Audience") { onDismiss() } }
    ) { paddingValues ->
        Content(paddingValues, mood, onEdited, onDismiss)
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    mood: DailyMoodEntity,
    onEdited: (DailyMoodEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    val selectedUsers = remember { mutableStateListOf<String>() }
//    selectedUsers.addAll(mood.audience.selectedUsers)

    LaunchedEffect(selectedUsers) {
        Log.d("Audience", "Selected Users: ${selectedUsers.size}")
    }

    var showCustomDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(paddingValues)) {
        getAudienceTypes().forEach { (type, description, icon) ->
            val isChecked = mood.audience.type == type

            AudienceItem(mood, type, description, icon, isChecked) {
                if (type == AudienceType.CUSTOM) {
                    showCustomDialog = true
                } else {
                    onEdited(mood.copy(audience = mood.audience.copy(type = type)))
                }
            }
        }
    }

    if (showCustomDialog) {
        Log.d("Audience", "Selected Users: ${selectedUsers.size}")

        UserSelectorDialog(
            headerLabel = "Select Users",
            userIds = mood.audience.selectedUsers,
            onUsersSelected = { result ->
                val userIds = (result as SnapshotStateList<*>).map { it.toString() }
                onEdited(mood.copy(audience = mood.audience.copy(selectedUsers = userIds)))
            },
            onDismiss = { showCustomDialog = false }
        )
    }
}

@Composable
fun AudienceItem(
    mood: DailyMoodEntity,
    type: AudienceType,
    description: String,
    @DrawableRes icon: Int,
    isChecked: Boolean,
    onSelected: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() }
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(PrimaryDark, CircleShape)
                    .padding(12.dp)
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            TypeAndDescription(mood, type, description)
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

@Composable
private fun TypeAndDescription(mood: DailyMoodEntity, type: AudienceType, description: String) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()

    Column {
        Text(
            text = type.toString().lowercase().replaceFirstChar { it.uppercase() },
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        if (type != AudienceType.CUSTOM) {
            Text(
                text = description,
                style = Typography.bodySmall.copy(color = GrayTextColor)
            )
        } else {
            val customAudienceList = mood.audience.selectedUsers

            val usernames = customAudienceList.mapNotNull {
                userViewModel.getUserById(it).collectAsState(initial = null).value?.username
            }

            val visibleUsers = usernames.take(3)
            val remainingCount = usernames.size - visibleUsers.size

            var joinedUsernames = visibleUsers.joinToString(", ")

            if (remainingCount > 0) {
                val othersLabel = if (remainingCount == 1) "1 other" else "$remainingCount others"
                joinedUsernames = "$joinedUsernames and $othersLabel"
            }

            Text(
                text = if (customAudienceList.isNotEmpty()) joinedUsernames else description,
                style = Typography.bodyMedium.copy(color = GrayTextColor),
                modifier = Modifier.widthIn(max = 256.dp)
            )
        }
    }
}

data class Audience(
    val type: AudienceType,
    val description: String,
    @DrawableRes val icon: Int,
)

private fun getAudienceTypes(): List<Audience> {
    return listOf(
        Audience(AudienceType.PUBLIC, "Visible to everyone", R.drawable.public_world),
        Audience(AudienceType.PRIVATE, "Visible only to you", R.drawable.private_user),
        Audience(AudienceType.FOLLOWERS, "Visible to your followers", R.drawable.followers),
        Audience(AudienceType.SUPPORTERS, "Visible to your supporters", R.drawable.supporter),
        Audience(AudienceType.CUSTOM, "Choose who can view your post", R.drawable.custom)
    )
}