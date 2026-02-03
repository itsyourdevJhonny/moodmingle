package com.emc.moodmingle.ui.post.comment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.remix.RemixEntity
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import kotlin.math.roundToInt

/*@Composable
fun CommentBottomSheet(remix: RemixEntity, onDismiss: () -> Unit) {
    var sheetOffset by remember { mutableFloatStateOf(0f) }
    val maxOffset = 600f

    var comment by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .windowInsetsPadding(WindowInsets.systemBars)
            .offset { IntOffset(0, sheetOffset.roundToInt()) }
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    sheetOffset = (sheetOffset + delta).coerceAtLeast(0f)
                },
                onDragStopped = { velocity ->
                    if (sheetOffset > maxOffset) {
                        onDismiss()
                    } else {
                        sheetOffset = 0f
                    }
                }
            )
    ) {
        Scaffold(
            topBar = {
                CommentSheetDragHandle(remix.comments.size.toLong(), remix.userId, onDismiss)
            },
            bottomBar = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    TextField(
                        value = comment,
                        onValueChange = { comment = it },
                        shape = RoundedCornerShape(8.dp),
                        placeholder = { Text(text = "Enter a comment...", color = GrayTextColor) },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = SecondaryDark,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = SecondaryDark,
                            focusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White
                        ),
                        modifier = Modifier.width(280.dp)
                    )

                    Box(modifier = Modifier.padding(end = 16.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.send),
                            contentDescription = "Send",
                            modifier = Modifier
                                .drawGradient()
                                .clickable {}
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(SecondaryDark)
            ) {

            }
        }
    }
}*/

@Composable
fun CommentBottomSheet(remix: RemixEntity, onDismiss: () -> Unit) {
    var sheetOffset by remember { mutableFloatStateOf(0f) }
    val maxOffset = 600f

    var comment by remember { mutableStateOf("") }

    val imeHeight = WindowInsets.ime.getBottom(LocalDensity.current)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .offset { IntOffset(0, sheetOffset.roundToInt()) }
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    sheetOffset = (sheetOffset + delta).coerceAtLeast(0f)
                },
                onDragStopped = { _ ->
                    if (sheetOffset > maxOffset) {
                        onDismiss()
                    } else {
                        sheetOffset = 0f
                    }
                }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SecondaryDark)
        ) {
            // DRAG HANDLE / HEADER
            CommentSheetDragHandle(
                totalComments = remix.comments.size.toLong(),
                userId = remix.userId,
                onDismiss = onDismiss
            )

            // COMMENTS AREA
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                // PLACEHOLDER FOR COMMENTS LIST
            }

            // INPUT ROW
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.Green)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                TextField(
                    value = comment,
                    onValueChange = { comment = it },
                    shape = RoundedCornerShape(8.dp),
                    placeholder = { Text(text = "Enter a comment...", color = GrayTextColor) },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = SecondaryDark,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = SecondaryDark,
                        focusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    painter = painterResource(R.drawable.send),
                    contentDescription = "Send",
                    modifier = Modifier
                        .size(36.dp)
                        .drawGradient()
                        .clickable {
                            // HANDLE SEND ACTION
                        }
                )
            }
        }
    }
}


@Composable
fun CommentSheetDragHandle(totalComments: Long, userId: String, onDismiss: () -> Unit) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val user by remember(userId) {
        userViewModel.getUserById(userId)
    }.collectAsState(initial = null)

    Column(
        modifier = Modifier.background(
            SecondaryDark,
            RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        )
    ) {
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
                text = " $totalComments ",
                style = Typography.bodyLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
            )

            Text(text = "Comments from ", style = Typography.bodyMedium.copy(color = GrayTextColor))

            val suffix = if (user?.username?.endsWith("s") == true) "s'" else "'s"

            Text(
                text = "${user?.username ?: ""}$suffix",
                style = Typography.bodyLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Black
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = " Post",
                style = Typography.bodyMedium.copy(color = GrayTextColor),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        DrawNoPaddingLine(thickness = 0.5.dp)
    }
}