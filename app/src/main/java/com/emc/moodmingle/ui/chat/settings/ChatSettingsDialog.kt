package com.emc.moodmingle.ui.chat.settings

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.chat.Conversation
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.components.dialogFullSizeProperties
import com.emc.moodmingle.viewmodel.remote.chat.ConversationViewModel
import kotlinx.coroutines.launch

@Composable
fun ChatSettingsDialog(
    conversation: Conversation?,
    conversationViewModel: ConversationViewModel,
    onDismiss: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = {},
        properties = dialogFullSizeProperties()
    ) {
        Column(
            modifier = Modifier
                .padding(top = 38.dp)
                .fillMaxSize()
                .background(PrimaryDark)
        ) {
            Box(modifier = Modifier
                .padding(start = 16.dp, bottom = 16.dp)
                .fillMaxWidth()) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.clickable { onDismiss() },
                    tint = Color.White
                )

                Text(
                    text = "Chat Settings",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column {
                CreateSetting(R.drawable.remove, "Delete this conversation") {
                    scope.launch {
                        conversationViewModel.deleteConversation(conversation!!)
                        Toast.makeText(context, "Conversation removed successfully", Toast.LENGTH_LONG).show()
                        onBack()
                    }
                }
                CreateSetting(R.drawable.pause_sound, "Mute") {}
            }
        }
    }
}

@Composable
private fun CreateSetting(@DrawableRes iconRes: Int, label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = if (label == "Delete this conversation") Color.Red else Color.White
            )

            Text(
                text = label
            )
        }
    }
}