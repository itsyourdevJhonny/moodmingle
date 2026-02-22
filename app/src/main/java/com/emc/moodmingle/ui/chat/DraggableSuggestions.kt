package com.emc.moodmingle.ui.chat

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.chat.Conversation
import com.emc.moodmingle.ui.chat.input.sendMessage
import com.emc.moodmingle.ui.chat.utils.InfiniteScrollingRow
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.remote.chat.ConversationViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun DraggableSuggestions(
    emojiSuggestions: List<String>,
    textSuggestions: List<String>,
    senderId: String,
    receiverId: String,
    conversation: Conversation,
    conversationViewModel: ConversationViewModel,
    textFieldValue: String
) {
    val scope = rememberCoroutineScope()

    var offsetY by remember { mutableFloatStateOf(0f) }

    val maxOffset = 160f

    val animatedOffsetY by animateFloatAsState(
        targetValue = offsetY,
        animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing)
    )

    var hideSuggestions by remember { mutableStateOf(false) }

    if (!hideSuggestions) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(PrimaryDark)
                .offset { IntOffset(0, animatedOffsetY.roundToInt()) }
                .drawBehind {
                    val strokeWidth = 0.3.dp.toPx()
                    drawLine(
                        brush = BrushPrimaryGradient,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = strokeWidth
                    )
                }
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { change, dragAmount ->
                            offsetY = (offsetY + dragAmount).coerceIn(0f, maxOffset)
                        },
                        onDragEnd = {
                            offsetY = if (offsetY > maxOffset / 2) maxOffset else 0f

                            if (offsetY > maxOffset / 2) {
                                hideSuggestions = true
                            }
                        },
                        onDragCancel = {
                            offsetY = 0f
                        }
                    )
                }
        ) {
            IconButton(onClick = { hideSuggestions = true }) {
                Icon(
                    painter = painterResource(R.drawable.drag_down),
                    contentDescription = "Drag Down",
                    modifier = Modifier
                        .graphicsLayer(alpha = 0.99f)
                        .drawGradient()
                )
            }

            InfiniteScrollingRow(emojiSuggestions, textFieldValue) { emojiSuggestion ->
                scope.launch {
                    sendMessage(
                        emojiSuggestion,
                        senderId,
                        receiverId,
                        conversation,
                        conversationViewModel
                    )
                }
            }

            InfiniteScrollingRow(textSuggestions, textFieldValue) { textSuggestion ->
                scope.launch {
                    sendMessage(
                        textSuggestion,
                        senderId,
                        receiverId,
                        conversation,
                        conversationViewModel
                    )
                }
            }
        }
    }
}
