package com.emc.moodmingle.ui.create.post.settings.pin

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.emc.moodmingle.ui.create.post.CreatePostDialogHeader
import com.emc.moodmingle.ui.post.action.toastMessage

@Composable
fun PinDialog(isPinned: Boolean, onPinChanged: (Boolean) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val localCopy = remember(Unit) { isPinned }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            CreatePostDialogHeader(
                label = "Pin Post",
                onBack = {
                    if (isPinned != localCopy) toastMessage(context, "Settings Saved")
                    onDismiss()
                }
            )
        }
    ) { paddingValues ->
        PinDialogContent(isPinned, paddingValues, onPinChanged)
    }
}