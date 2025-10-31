package com.emc.moodmingle.ui.post.action

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.AngryColor
import com.emc.moodmingle.ui.theme.HappyColor
import com.emc.moodmingle.ui.theme.HeartColor
import com.emc.moodmingle.ui.theme.SadColor
import com.emc.moodmingle.ui.theme.ScaryColor
import com.emc.moodmingle.ui.theme.WowColor
import kotlin.collections.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReactionAction(globalLikesState: MutableState<Int>) {
    var isReacted by remember { mutableStateOf(false) }

    var selectedReaction by remember { mutableStateOf<Int?>(null) }

    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val displayIcon = when (selectedReaction) {
        R.drawable.scary -> R.drawable.scary
        R.drawable.happy -> R.drawable.happy
        R.drawable.wow -> R.drawable.wow
        R.drawable.sad -> R.drawable.sad
        R.drawable.angry -> R.drawable.angry
        else -> null
    }

    val displayColor = mapOf(
        R.drawable.scary to ScaryColor,
        R.drawable.happy to HappyColor,
        R.drawable.wow to WowColor,
        R.drawable.sad to SadColor,
        R.drawable.angry to AngryColor
    )

    Box(
        modifier = Modifier
            .background(Color.Transparent)
            .border(
                width = 1.dp,
                color = displayColor[selectedReaction] ?: Color.White,
                shape = CircleShape
            )
    ) {
        if (displayIcon != null) {
            Icon(
                painter = painterResource(displayIcon),
                contentDescription = "Reaction Icon",
                modifier = Modifier
                    .padding(10.dp)
                    .size(32.dp)
                    .combinedClickable(
                        onClick = {
                            isReacted = false
                            selectedReaction = null
                            globalLikesState.value--
                        },
                        onLongClick = {
                            showSheet = true
                        }
                    ),
                tint = displayColor[selectedReaction] ?: Color.Unspecified
            )
        } else {
            Icon(
                modifier = Modifier
                    .padding(10.dp)
                    .size(32.dp)
                    .combinedClickable(
                        onClick = {
                            isReacted = !isReacted
                            if (isReacted) globalLikesState.value++ else globalLikesState.value--
                        },
                        onLongClick = {
                            showSheet = true
                        }
                    ),
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favorite",
                tint = if (isReacted) HeartColor else Color.White
            )
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = Color(0xFF121212)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Choose your reaction",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val reactions = listOf(
                        R.drawable.scary to "Scary",
                        R.drawable.happy to "Happy",
                        R.drawable.wow to "Wow",
                        R.drawable.sad to "Sad",
                        R.drawable.angry to "Angry"
                    )

                    reactions.forEach { (emoji, label) ->
                        var clicked by remember { mutableStateOf(false) }
                        val scale by animateFloatAsState(
                            targetValue = if (clicked) 1.4f else 1f,
                            animationSpec = tween(
                                durationMillis = 150,
                                easing = FastOutSlowInEasing
                            ),
                            finishedListener = { clicked = false }
                        )

                        val isReactedAndSelected = isReacted && emoji == selectedReaction

                        val reactedColor = if (isReactedAndSelected) {
                            displayColor[selectedReaction]?.copy(0.5f) ?: Color.White
                        } else {
                            Color.White
                        }

                        val reactedSize = if (isReactedAndSelected) 62.dp else 45.dp

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .graphicsLayer(scaleX = scale, scaleY = scale)
                                .clickable {
                                    clicked = true
                                    showSheet = false

                                    if (selectedReaction == emoji) {
                                        selectedReaction = null
                                        isReacted = false
                                        globalLikesState.value--
                                    } else {
                                        if (!isReacted) {
                                            globalLikesState.value++
                                        }
                                        selectedReaction = emoji
                                        isReacted = true
                                    }
                                }
                        ) {
                            Icon(
                                modifier = Modifier.size(reactedSize),
                                painter = painterResource(emoji),
                                contentDescription = label,
                                tint = displayColor[emoji] ?: Color.Unspecified
                            )

                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = reactedColor,
                                    fontWeight = if (isReactedAndSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = if (isReactedAndSelected) 16.sp else TextUnit.Unspecified
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}