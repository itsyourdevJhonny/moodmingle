package com.emc.moodmingle.ui.chat.reply

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.chat.ChatMessage
import com.emc.moodmingle.ui.theme.BrushSecondaryDarkGradient
import com.emc.moodmingle.ui.theme.BrushSecondaryTertiaryGradient
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.firebase.PostViewModelFirebase

@Composable
fun PostMessageRepliedContent(message: ChatMessage, isOwn: Boolean) {
    Column(
        modifier = Modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val postViewModelFirebase = hiltViewModel<PostViewModelFirebase>()
        val post by postViewModelFirebase.getPostById(message.postId).collectAsState(initial = null)

        val informationTypes = listOf(
            post?.hashtag to R.drawable.hashtag,
            post?.caption to R.drawable.caption,
            post?.description to R.drawable.description
        )

        Box(
            modifier = Modifier.background(
                brush = if (isOwn) BrushSecondaryTertiaryGradient else BrushSecondaryDarkGradient,
                shape = RoundedCornerShape(8.dp)
            )
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Box {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AsyncImage(
                            model = post?.avatarUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = post?.username ?: "",
                            style = Typography.bodyMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                informationTypes.forEach { informationType ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(informationType.second),
                            contentDescription = "Hashtag",
                            modifier = Modifier
                                .size(14.dp)
                                .graphicsLayer(alpha = 0.99f)
                                .drawGradient()
                        )

                        Text(
                            text = informationType.first?.replace("#", "") ?: "",
                            style = Typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Image(
                painter = painterResource(R.drawable.message_colored),
                contentDescription = "Message",
                modifier = Modifier
                    .rotate(-50f)
                    .size(42.dp)
                    .offset(x = (-30).dp, y = (-30).dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(vertical = 4.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.reply),
                contentDescription = "Reply",
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )

            Text(
                text = if (isOwn) "You replied" else "Replied to you",
                style = Typography.bodySmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.W900,
                    fontStyle = FontStyle.Italic
                )
            )
        }

        Box(
            modifier = Modifier
                .background(
                    brush = if (isOwn) BrushSecondaryTertiaryGradient else BrushSecondaryDarkGradient,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Text(
                text = message.message,
                color = Color.White,
                style = Typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}