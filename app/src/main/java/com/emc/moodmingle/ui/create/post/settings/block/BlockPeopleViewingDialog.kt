package com.emc.moodmingle.ui.create.post.settings.block

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.emc.moodmingle.ui.create.post.CreatePostDialogHeader
import com.emc.moodmingle.ui.post.action.toastMessage
import com.emc.moodmingle.utils.components.UserSelector

@Composable
fun BlockPeopleViewingDialog(
    blockedUserIds: List<String>,
    onUsersBlocked: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val localCopy = remember(Unit) { blockedUserIds }
    var showSelectUserDialog by remember { mutableStateOf(false) }

    Box {
        Scaffold(
            containerColor = Color.Black,
            topBar = {
                CreatePostDialogHeader(
                    label = "Block People from Viewing",
                    onBack = {
                        if (blockedUserIds.size != localCopy.size) {
                            toastMessage(context, "Settings Saved")
                        }
                        onDismiss()
                    }
                )
            }
        ) { paddingValues ->
            PeopleViewingDialogContent(
                paddingValues,
                blockedUserIds,
                onUsersBlocked,
                onShowSelectUserDialog = { showSelectUserDialog = it }
            )
        }

        if (showSelectUserDialog) {
            UserSelector(
                title = "Select User to Block",
                userIds = blockedUserIds.toList(),
                onUsersSelected = { data ->
                    val userIds = (data as SnapshotStateList<*>).map { it.toString() }
                    onUsersBlocked(userIds)
                },
                onDismiss = { showSelectUserDialog = false }
            )
        }
    }
}