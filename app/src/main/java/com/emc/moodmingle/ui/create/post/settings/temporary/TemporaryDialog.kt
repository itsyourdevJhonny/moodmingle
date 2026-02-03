package com.emc.moodmingle.ui.create.post.settings.temporary

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.emc.moodmingle.data.firebase.model.post.settings.PostTemporary
import com.emc.moodmingle.ui.create.post.CreatePostDialogHeader
import com.emc.moodmingle.ui.post.action.toastMessage

@Composable
fun TemporaryDialog(
    temporary: PostTemporary,
    onTemporaryChanged: (PostTemporary) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val localCopy = remember(Unit) { temporary }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            CreatePostDialogHeader(
                label = "Make Post Temporary",
                onBack = {
                    if (temporary != localCopy) toastMessage(context, "Settings Saved")
                    onDismiss()
                }
            )
        }
    ) { paddingValues ->
        TemporaryDialogContent(paddingValues, temporary, onTemporaryChanged)
    }
}