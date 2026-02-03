package com.emc.moodmingle.ui.post.action

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.ui.post.comment.DisplayComment
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.firebase.CommentViewModelFirebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentAction(postEntity: PostEntityFirebase, onChatClick: (String, String) -> Unit) {
    val commentViewModelFirebase = hiltViewModel<CommentViewModelFirebase>()

    val comments by commentViewModelFirebase.getCommentCountByPostId(postEntity.id)
        .collectAsState(initial = 0)

    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(
        modifier = Modifier
            .background(SecondaryDark, CircleShape)
            .size(40.dp)
            .clickable { showSheet = true },
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
            Column(modifier = Modifier.fillMaxHeight(0.9f).background(PrimaryDark)) {
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
                DisplayComment(postEntity, onChatClick)
            }
        }
    }
}