package com.emc.moodmingle.ui.dailymood.settings.block

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.settings.saved.utils.EmptyComponent
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.ScaffoldHeader
import com.emc.moodmingle.utils.components.UserSelector
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockedPeopleScreen(
    settings: DailyMoodSettings,
    onEdit: (DailyMoodSettings) -> Unit,
    onBack: () -> Unit,
) {
    var showUserSelector by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Black,
        topBar = { ScaffoldHeader(title = "Exclude People") { onBack() } }
    ) { paddingValues ->
        Content(paddingValues, settings, onEdit) { showUserSelector = true }
    }

    if (showUserSelector) {
        UserSelector(
            title = "Select People",
            userIds = settings.blockedUserIds.toList(),
            onUsersSelected = { result ->
                val selectedUserIds = (result as SnapshotStateList<*>).map { it.toString() }
                onEdit(settings.copy(blockedUserIds = selectedUserIds.toSet()))
            },
            onDismiss = { showUserSelector = false }
        )
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    settings: DailyMoodSettings,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
    onSelect: () -> Unit,
) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val blockedIds = settings.blockedUserIds

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Description()

        Spacer(modifier = Modifier.height(12.dp))

        SelectPeopleButton(onSelect, blockedIds)

        HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(bottom = 16.dp))

        if (blockedIds.isNotEmpty()) {
            BlockedCounter(blockedIds)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (blockedIds.isEmpty()) {
            EmptyComponent(R.drawable.block_user, "No people excluded.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(items = blockedIds.toList(), key = { it }) { userId ->
                    val user by userViewModel.getUserById(userId).collectAsState(initial = null)
                    BlockedUserItem(user) { onSettingsEdited(settings.copy(blockedUserIds = settings.blockedUserIds - userId)) }
                }
            }
        }
    }
}

@Composable
private fun Description() {
    Text(
        text = "Selected people won’t be able to view or interact with this mood.",
        color = Color.White,
        style = Typography.bodyMedium
    )
}

@Composable
private fun SelectPeopleButton(onSelect: () -> Unit, blockedIds: Set<String>) {
    TextButton(
        onClick = onSelect,
        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Select ${if (blockedIds.isNotEmpty()) "more" else ""} people",
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.chevron_right),
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun BlockedCounter(blockedIds: Set<String>) {
    Text(
        text = "${blockedIds.size} people excluded",
        color = GrayTextColor,
        style = Typography.bodyMedium
    )
}

@Composable
private fun LazyItemScope.BlockedUserItem(user: UserEntityFirebase?, onRemove: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.14f)),
        modifier = Modifier.animateItem()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ItemAvatar(user)

            Spacer(modifier = Modifier.width(12.dp))

            ItemUsername(user)
            ItemRemoveButton(onRemove)
        }
    }
}

@Composable
private fun ItemAvatar(user: UserEntityFirebase?) {
    AsyncImage(
        model = user?.avatarUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .gradientCircleBorder()
    )
}

@Composable
private fun RowScope.ItemUsername(user: UserEntityFirebase?) {
    Text(
        text = user?.username.orEmpty(),
        color = Color.White,
        modifier = Modifier.weight(1f),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun ItemRemoveButton(onRemove: () -> Unit) {
    IconButton(onClick = onRemove) {
        Icon(
            painter = painterResource(R.drawable.remove),
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(24.dp)
        )
    }
}
