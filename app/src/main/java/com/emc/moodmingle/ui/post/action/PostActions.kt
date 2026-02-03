package com.emc.moodmingle.ui.post.action

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.chat.checkConversationAndSendMessage
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.chat.ConversationViewModel
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun PostActions(
    entityId: String,
    ownerId: String,
    isReacted: Boolean,
    reactions: Long,
    comments: Long,
    shares: Long,
    onReact: () -> Unit,
    onComment: () -> Unit,
    onShare: () -> Unit,
    onChat: (String, String) -> Unit
) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val currentUser by userViewModel.loggedUser
    val currentUserId = currentUser?.uid.orEmpty()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionContainer(reactions) { Reaction(isReacted, onReact) }
        ActionContainer(comments) { Comment(comments, onComment, onChat) }
        if (currentUserId != ownerId) Chat(currentUserId, ownerId, entityId, onChat)
        ActionContainer(shares) { Share(onShare) }
    }
}

@Composable
fun ActionContainer(count: Long, content: @Composable () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        content()

        if (count > 0) {
            Text(text = "$count", style = Typography.bodyMedium.copy(color = GrayTextColor))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Reaction(isReacted: Boolean, onReact: () -> Unit) {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val floatingHearts = remember {
        mutableStateListOf<Triple<Float, Animatable<Float, AnimationVector1D>, Float>>()
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clickable {
                if (!isReacted) {
                    scope.launch {
                        scale.animateTo(
                            1.5f,
                            animationSpec = tween(500, easing = LinearOutSlowInEasing)
                        )
                        scale.animateTo(
                            1f,
                            animationSpec = tween(500, easing = LinearOutSlowInEasing)
                        )
                    }

                    repeat(16) { index ->
                        scope.launch {
                            delay(index * 100L)

                            val animY = Animatable(0f)
                            val randomX = (-15..15).random().toFloat()
                            val randomScale = 0.8f + Random.nextFloat() * 0.4f

                            floatingHearts.add(Triple(randomX, animY, randomScale))

                            animY.animateTo(
                                targetValue = -150f - Random.nextFloat() * 30f,
                                animationSpec = tween(
                                    durationMillis = 800 + Random.nextInt(200),
                                    easing = LinearOutSlowInEasing
                                )
                            )

                            floatingHearts.remove(Triple(randomX, animY, randomScale))
                        }
                    }
                }

                onReact()
            }
            .background(SecondaryDark, CircleShape)
            .border(
                width = 1.dp,
                color = if (isReacted) Color.Red else Color.White,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.love),
            contentDescription = "Reaction Icon",
            modifier = Modifier
                .size(20.dp)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                },
            tint = if (isReacted) Color.Red else Color.White
        )

        floatingHearts.forEach { (xOffset, animY, scaleFactor) ->
            Icon(
                painter = painterResource(R.drawable.love),
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer {
                        scaleX = scaleFactor
                        scaleY = scaleFactor
                    }
                    .offset(x = xOffset.dp, y = animY.value.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Comment(comments: Long, onComment: () -> Unit, onChat: (String, String) -> Unit) {
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(
        modifier = Modifier
            .background(SecondaryDark, CircleShape)
            .size(40.dp)
            .clickable { onComment() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.comment),
            contentDescription = "Comment",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = PrimaryDark,
            dragHandle = {
                Icon(
                    painter = painterResource(R.drawable.comment),
                    contentDescription = "Comment",
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .size(26.dp)
                        .drawGradient()
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .background(PrimaryDark)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Comments $comments",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )

                DrawNoPaddingLine(modifier = Modifier.padding(top = 10.dp))
//                DisplayComment(postEntity, onChat)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Share(onShare: () -> Unit) {
    Box(
        modifier = Modifier
            .background(SecondaryDark, CircleShape)
            .size(40.dp)
            .clickable { onShare() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.share),
            contentDescription = "Share",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun Chat(
    currentUserId: String,
    ownerId: String,
    entityId: String,
    onChat: (String, String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val conversationViewModel = hiltViewModel<ConversationViewModel>()

    Box(
        modifier = Modifier
            .background(SecondaryDark, CircleShape)
            .size(40.dp)
            .clickable {
                checkConversationAndSendMessage(
                    senderId = currentUserId,
                    receiverId = ownerId,
                    entityId = entityId,
                    type = "NORMAL_POST",
                    scope = scope,
                    conversationViewModel = conversationViewModel
                )

                onChat(currentUserId, ownerId)
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.chat),
            contentDescription = "Share",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}