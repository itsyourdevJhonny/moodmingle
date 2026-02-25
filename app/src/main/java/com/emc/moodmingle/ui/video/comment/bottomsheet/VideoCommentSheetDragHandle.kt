package com.emc.moodmingle.ui.video.comment.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.viewmodel.remote.PostViewModelFirebase

@Composable
fun VideoCommentSheetDragHandle(commentsCount: Long, videoUrl: String, onDismiss: () -> Unit) {
    val postViewModel = hiltViewModel<PostViewModelFirebase>()
    val post by remember(videoUrl) {
        postViewModel.getPostByVideoUrl(videoUrl)
    }.collectAsState(initial = null)

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onDismiss() },
                tint = Color.White
            )

            Text(
                text = " $commentsCount ",
                style = Typography.bodyLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
            )

            Text(text = "Comments from ", style = Typography.bodyMedium.copy(color = GrayTextColor))

            val suffix = if (post?.username?.endsWith("s") == true) "s'" else "'s"
            Text(
                text = "${post?.username ?: ""}$suffix",
                style = Typography.bodyLarge.copy(color = Color.White, fontWeight = FontWeight.Black),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = " Video",
                style = Typography.bodyMedium.copy(color = GrayTextColor),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        DrawNoPaddingLine(thickness = 0.5.dp)
    }
}