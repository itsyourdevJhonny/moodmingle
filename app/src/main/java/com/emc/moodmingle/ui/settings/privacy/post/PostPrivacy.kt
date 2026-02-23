package com.emc.moodmingle.ui.settings.privacy.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel
import kotlinx.coroutines.launch

@Composable
fun PostPrivacy(userEntityFirebase: UserEntityFirebase) {
    Column {
        ChatVisibility(userEntityFirebase)
        HiddenPosts(userEntityFirebase)
    }
}

@Composable
fun ChatVisibility(userEntityFirebase: UserEntityFirebase) {
    val scope = rememberCoroutineScope()
    val userViewModelFirebase = hiltViewModel<FirebaseUserViewModel>()
    var isDisabled by remember(userEntityFirebase) { mutableStateOf(userEntityFirebase.chatDisabled) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                scope.launch {
                    userViewModelFirebase.updateUser(userEntityFirebase.copy(chatDisabled = !isDisabled))
                }
            },
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.chat),
                contentDescription = "Chat",
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Chat ", style = Typography.bodyMedium)

                    Text(
                        text = "(${if (isDisabled) "Disabled" else "Enabled"})",
                        style = Typography.bodyMedium.copy(color = GrayTextColor)
                    )
                }

                Icon(
                    painter = painterResource(if (isDisabled) R.drawable.visibility_off else R.drawable.visibility_on),
                    contentDescription = "Enable/Disable",
                    modifier = Modifier.size(20.dp),
                    tint = if (isDisabled) Color.Red else Color.Green
                )
            }
        }
    }
}