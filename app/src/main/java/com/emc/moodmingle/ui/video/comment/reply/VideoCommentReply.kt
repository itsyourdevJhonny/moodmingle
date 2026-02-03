package com.emc.moodmingle.ui.video.comment.reply

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.TertiaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun ColumnScope.VideoCommentReply(
    commenter: UserEntityFirebase?,
    replyText: String,
    isSelected: Boolean
) {
    AnimatedVisibility(
        visible = isSelected,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        Column(modifier = Modifier.align(Alignment.End)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.reply_right),
                    contentDescription = "Reply",
                    modifier = Modifier
                        .size(16.dp)
                        .drawGradient()
                )

                Text(text = " Reply to ", style = Typography.bodySmall.copy(color = GrayTextColor))

                Text(
                    text = "${commenter?.username}",
                    style = Typography.bodySmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                )
            }

            Box(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .width(250.dp)
                    .heightIn(min = 40.dp)
                    .background(SecondaryDark, RoundedCornerShape(8.dp))
                    .border(width = 0.5.dp, color = TertiaryDark, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = replyText.ifBlank { "Enter your reply..." },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    style = Typography.bodyMedium.copy(color = if (replyText.isBlank()) GrayTextColor else Color.White)
                )
            }
        }
    }
}