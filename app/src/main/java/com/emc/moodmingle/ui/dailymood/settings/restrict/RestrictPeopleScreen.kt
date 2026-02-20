package com.emc.moodmingle.ui.dailymood.settings.restrict

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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.Avatar
import com.emc.moodmingle.utils.components.ScaffoldHeader
import com.emc.moodmingle.utils.components.UserSelector
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@Composable
fun RestrictAccountsScreen(
    settings: DailyMoodSettings,
    onEdit: (DailyMoodSettings) -> Unit,
    onBack: () -> Unit,
) {
    var showUserSelector by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Black,
        topBar = { ScaffoldHeader(title = "Restrict From Mood") { onBack() } }
    ) { paddingValues ->
        Content(paddingValues, settings, onEdit) { showUserSelector = true }
    }

    if (showUserSelector) {
        UserSelector(
            title = "Select People",
            userIds = settings.restrictedUserIds.toList(),
            onUsersSelected = { result ->
                val selectedUserIds = (result as SnapshotStateList<*>).map { it.toString() }
                onEdit(settings.copy(restrictedUserIds = selectedUserIds.toSet()))
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
    val restrictedIds = settings.restrictedUserIds

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Description()

        Spacer(modifier = Modifier.height(12.dp))

        SelectPeopleButton(onSelect, restrictedIds)

        HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(bottom = 16.dp))

        if (restrictedIds.isNotEmpty()) {
            RestrictedCounter(restrictedIds)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items = restrictedIds.toList(), key = { it }) { userId ->
                val user by userViewModel.getUserById(userId).collectAsState(initial = null)

                RestrictUserItem(
                    user = user,
                    onRemove = { onSettingsEdited(settings.copy(restrictedUserIds = settings.restrictedUserIds - userId)) }
                )
            }
        }
    }
}

@Composable
private fun Description() {
    Text(
        text = "Restricted people can view this mood but cannot interact with it.",
        color = Color.White,
        style = Typography.bodyMedium
    )
}

@Composable
private fun SelectPeopleButton(onSelect: () -> Unit, restrictedIds: Set<String>) {
    TextButton(
        onClick = onSelect,
        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Select ${if (restrictedIds.isNotEmpty()) "more" else ""} people",
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
private fun RestrictedCounter(restrictedIds: Set<String>) {
    Text(
        text = "${restrictedIds.size} People Restricted",
        color = GrayTextColor,
        style = Typography.bodyMedium
    )
}

@Composable
private fun LazyItemScope.RestrictUserItem(user: UserEntityFirebase?, onRemove: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Yellow.copy(alpha = 0.1f)),
        modifier = Modifier.animateItem()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(user?.avatarUrl.orEmpty(), 40.dp)

            Spacer(modifier = Modifier.width(12.dp))

            ItemUsernameAndLabel(user)

            ItemRemoveButton(onRemove)
        }
    }
}

@Composable
private fun RowScope.ItemUsernameAndLabel(user: UserEntityFirebase?) {
    Column(modifier = Modifier.weight(1f)) {
        Text(
            text = user?.username.orEmpty(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(text = "Restricted", color = Color.White, style = Typography.bodySmall)
    }
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

