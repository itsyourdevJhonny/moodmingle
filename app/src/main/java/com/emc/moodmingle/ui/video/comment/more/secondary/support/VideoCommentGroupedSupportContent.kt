package com.emc.moodmingle.ui.video.comment.more.secondary.support

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.data.firebase.model.video.Support
import com.emc.moodmingle.data.model.post.formatTimeAgo
import com.emc.moodmingle.ui.post.text.ExpandableAutoDetectClickableText
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.utils.modifier.roundedGrayBorder
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@Composable
fun VideoCommentGroupedSupportContent(content: Any) {
    val support = content as Support

    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val supporter by remember(support.supporterId) {
        userViewModel.getUserById(support.supporterId)
    }.collectAsState(initial = null)

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        SupporterAvatar(supporter)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SupporterUsername(supporter)
            SupportMessageAndType(support)
            SupportTimestamp(support)
        }
    }
}

@Composable
private fun SupportTimestamp(support: Support) {
    Text(
        text = formatTimeAgo(support.timestamp),
        style = Typography.bodySmall.copy(GrayTextColor)
    )
}

@Composable
private fun SupportMessageAndType(support: Support) {
    Box(
        modifier = Modifier
            .background(SecondaryDark, RoundedCornerShape(8.dp))
            .roundedGrayBorder(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExpandableAutoDetectClickableText(
                fullText = support.message,
                style = Typography.bodyLarge.copy(color = Color.White),
                hasPadding = false
            )

            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .background(Color.Green.copy(alpha = 0.5f), CircleShape)
            ) {
                Text(
                    text = support.supportType.ifEmpty { "None" },
                    style = Typography.bodyMedium.copy(color = Color.White),
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun SupporterUsername(supporter: UserEntityFirebase?) {
    Text(
        text = supporter?.username ?: "",
        style = Typography.bodyLarge.copy(color = Color.White, fontWeight = FontWeight.Black),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.widthIn(max = 160.dp)
    )
}

@Composable
private fun SupporterAvatar(supporter: UserEntityFirebase?) {
    AsyncImage(
        model = supporter?.avatarUrl,
        contentDescription = "Avatar",
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .gradientCircleBorder(),
        contentScale = ContentScale.Crop
    )
}