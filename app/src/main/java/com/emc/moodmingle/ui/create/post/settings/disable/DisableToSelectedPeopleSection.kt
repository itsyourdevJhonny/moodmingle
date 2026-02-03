package com.emc.moodmingle.ui.create.post.settings.disable

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.settings.PostCommentReactionVisibility
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.rememberUsersByIds
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.utils.modifier.roundedGrayBorder
import com.emc.moodmingle.utils.text.NumberFormatter
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@Composable
fun DisableToSelectedPeopleSection(
    userViewModel: FirebaseUserViewModel,
    isComment: Boolean,
    enabled: Boolean,
    commentReactionVisibility: PostCommentReactionVisibility,
    onShowSelectUserDialog: (Boolean) -> Unit
) {
    if (isComment && !enabled) {
        Column(
            modifier = Modifier.padding(start = 28.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Label(commentReactionVisibility)

            if (commentReactionVisibility.selectedUserIds.isEmpty()) {
                SelectButton(label = "Tap to select user", onShowSelectUserDialog)
            } else {
                SelectedPeople(
                    commentReactionVisibility,
                    userViewModel,
                    onShowSelectUserDialog
                )
            }
        }
    }
}

@Composable
private fun Label(commentReactionVisibility: PostCommentReactionVisibility) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(R.drawable.block_user),
            contentDescription = "Disabled",
            modifier = Modifier
                .size(18.dp)
                .drawGradient()
        )

        Text(
            text = " Disable to selected people only ",
            style = Typography.bodyMedium.copy(color = Color.White)
        )

        Text(
            text = "(${commentReactionVisibility.selectedUserIds.size})",
            style = Typography.bodyMedium.copy(color = GrayTextColor)
        )
    }
}

@Composable
private fun SelectedPeople(
    commentReactionVisibility: PostCommentReactionVisibility,
    userViewModel: FirebaseUserViewModel,
    onShowSelectUserDialog: (Boolean) -> Unit
) {
    val selectedUsers by rememberUsersByIds(
        commentReactionVisibility.selectedUserIds,
        userViewModel
    )

    val visibleSize = 3

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.animateContentSize()
    ) {
        selectedUsers.take(visibleSize).forEach { user ->
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .gradientCircleBorder(),
                contentScale = ContentScale.Crop
            )
        }

        if (selectedUsers.size > visibleSize) {
            val formattedValue = NumberFormatter.formatValue(
                value = (selectedUsers.size - visibleSize).toLong(),
                includeDecimal = true
            )

            Text(text = "+$formattedValue", color = GrayTextColor)
        }

        SelectButton(label = "Select More", onShowSelectUserDialog)
    }
}

@Composable
private fun SelectButton(label: String, onShowSelectUserDialog: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onShowSelectUserDialog(true) }
            .background(PrimaryDark, CircleShape)
            .roundedGrayBorder(16.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = label,
            modifier = Modifier.size(20.dp)
        )

        Text(text = " $label", style = Typography.bodySmall.copy(color = Color.White))
    }
}