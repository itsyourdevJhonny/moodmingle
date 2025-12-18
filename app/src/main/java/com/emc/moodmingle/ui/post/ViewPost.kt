package com.emc.moodmingle.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.emc.moodmingle.data.firebase.model.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.UserEntityFirebase
import com.emc.moodmingle.ui.post.text.ExpandableAutoDetectClickableText
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.dialogFullSizeProperties
import com.emc.moodmingle.utils.text.TextFormatter

@Composable
fun ViewPost(
    postEntity: PostEntityFirebase,
    userEntity: UserEntityFirebase?,
    onClick: (String) -> Unit,
    showShareSheet: Boolean,
    onShowShareSheet: (Boolean) -> Unit,
    onChatClick: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = dialogFullSizeProperties()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryDark),
            contentAlignment = Alignment.Center
        ) {
            Header(userEntity, modifier = Modifier.align(Alignment.TopStart), onDismiss)

            if (postEntity.urls.isNotEmpty()) {
                MultimediaCard(
                    isCLickable = false,
                    composable = {
                        PostMedia(
                            mediaUrls = postEntity.urls,
                            onShowShareSheet
                        )
                    },
                    postEntity = postEntity,
                    userEntity = userEntity,
                    postType = "IMAGE",
                    onClick = onClick,
                    showShareSheet = showShareSheet,
                    onShowShareSheet = onShowShareSheet,
                    onChatClick = onChatClick
                )
            } else {
                MultimediaCard(
                    isCLickable = false,
                    composable = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            content = {
                                ExpandableAutoDetectClickableText(
                                    fullText = postEntity.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    hasPadding = false,
                                )
                            }
                        )
                    },
                    postEntity = postEntity,
                    userEntity = userEntity,
                    postType = "TEXT",
                    onClick = onClick,
                    showShareSheet = showShareSheet,
                    onShowShareSheet = onShowShareSheet,
                    onChatClick = onChatClick
                )
            }
        }
    }
}

@Composable
private fun Header(
    userEntity: UserEntityFirebase?,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(top = 38.dp)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.clickable { onDismiss() },
                tint = Color.White
            )

            Text(
                text = TextFormatter.formatTextWithSuffixS(userEntity?.username ?: ""),
                style = Typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 250.dp)
            )

            Text(
                text = "Post",
                style = Typography.bodyMedium.copy(color = Color.White)
            )
        }
    }
}