package com.emc.moodmingle.ui.settings.privacy.account

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.SwitchButton
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import kotlinx.coroutines.launch

@Composable
fun AccountVisibility(userEntity: UserEntityFirebase) {
    val scope = rememberCoroutineScope()
    val userViewModelFirebase = hiltViewModel<FirebaseUserViewModel>()
    var isPrivate by remember(userEntity) { mutableStateOf(userEntity.private) }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(if (!isPrivate) R.drawable.public_account else R.drawable.private_account),
                contentDescription = "Public/Private",
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )

            SwitchButton(
                label = if (!isPrivate) "Public" else "Private",
                isChecked = isPrivate,
                onCheckedChange = {
                    scope.launch { userViewModelFirebase.updateUser(userEntity.copy(private = it)) }
                }
            )
        }

        Text(
            text = if (!isPrivate) "A private account limits your profile, posts, and activity so that only approved followers can view them."
            else "A public account allows anyone to view your profile, posts, and activity.",
            style = Typography.bodySmall.copy(color = GrayTextColor, fontStyle = FontStyle.Italic),
            modifier = Modifier.padding(start = 28.dp)
        )
    }
}