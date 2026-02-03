package com.emc.moodmingle.ui.video.comment.information

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.data.firebase.model.video.VideoComment
import com.emc.moodmingle.data.model.post.formatTimeAgo
import com.emc.moodmingle.ui.post.text.ExpandableAutoDetectClickableText
import com.emc.moodmingle.ui.screens.getEmojiByEmotion
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.TertiaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun VideoCommentInformation(
    commenter: UserEntityFirebase?,
    comment: VideoComment?,
    onShowMoreAction: (Boolean) -> Unit,
    onUserClick: (String) -> Unit
) {
    AnimatedVisibility(
        visible = comment != null && commenter != null,
        enter = fadeIn(animationSpec = tween(300)) + expandHorizontally(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(animationSpec = tween(300))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onUserClick(comment?.commenterId ?: "") }
                ) {
                    AtSymbol()
                    CommenterUsername(commenter)
                    VerifiedIcon(commenter)
                }

                MoreIcon(onShowMoreAction)
            }

            TimeAgo(comment)
            CommentText(comment)
        }
    }
}

@Composable
private fun AtSymbol() {
    Text(
        text = "@",
        style = Typography.bodyLarge.copy(
            brush = BrushPrimaryGradient,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
private fun CommenterUsername(commenter: UserEntityFirebase?) {
    Text(
        text = "${commenter?.username}",
        style = Typography.bodyMedium.copy(
            color = Color.White,
            fontWeight = FontWeight.Black
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.widthIn(max = 250.dp)
    )
}

@Composable
private fun VerifiedIcon(commenter: UserEntityFirebase?) {
    if (commenter?.verified == true) {
        Icon(
            painter = painterResource(R.drawable.verified),
            contentDescription = "Verified",
            modifier = Modifier
                .size(16.dp)
                .drawGradient()
        )
    }
}

@Composable
private fun MoreIcon(onShowMoreAction: (Boolean) -> Unit) {
    Icon(
        painter = painterResource(R.drawable.more),
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.clickable { onShowMoreAction(true) }
    )
}

@Composable
private fun TimeAgo(comment: VideoComment?) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = formatTimeAgo(comment?.timestamp ?: 0),
            style = Typography.bodySmall.copy(color = GrayTextColor)
        )

        if (comment?.emotion?.isNotBlank() == true) {
            Box(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .background(BrushPrimaryGradient, CircleShape)
            ) {
                Text(
                    text = "${getEmojiByEmotion()[comment.emotion]} ${comment.emotion}",
                    style = Typography.bodyMedium.copy(color = Color.White),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun CommentText(comment: VideoComment?) {
    if (comment?.comment?.isNotBlank() == true) {
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .background(SecondaryDark, RoundedCornerShape(8.dp))
                .border(
                    width = 0.5.dp,
                    color = TertiaryDark,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .animateContentSize(),
                contentAlignment = Alignment.Center
            ) {
                ExpandableAutoDetectClickableText(
                    fullText = comment.comment,
                    style = Typography.bodyLarge.copy(color = Color.White),
                    hasPadding = false,
                    minLines = 5
                )
            }
        }
    }
}