package com.emc.moodmingle.ui.settings.saved

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.saved.SaveEntityFirebase
import com.emc.moodmingle.data.model.post.formatTimeAgo
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.viewmodel.firebase.CommentViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.ReactionViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.ShareViewModelFirebase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@Composable
fun PostStatistics(post: PostEntityFirebase, save: SaveEntityFirebase) {
    val scope = rememberCoroutineScope()
    val commentViewModelFirebase = hiltViewModel<CommentViewModelFirebase>()
    val reactionViewModelFirebase = hiltViewModel<ReactionViewModelFirebase>()
    val shareViewModelFirebase = hiltViewModel<ShareViewModelFirebase>()

    val postId = post.id

    val commentCount by remember(postId) {
        commentViewModelFirebase.getCommentCountByPostId(post.id)
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), 0)
    }.collectAsState(initial = 0)

    val reactionCount by remember(postId) {
        reactionViewModelFirebase.getReactionsCountByPostId(post.id)
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), 0)
    }.collectAsState(initial = 0)

    val shareCount by remember(postId) {
        shareViewModelFirebase.getShareCountByPostId(post.id)
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), 0)
    }.collectAsState(initial = 0)

    val statistics = listOf(
        commentCount to R.drawable.comment,
        reactionCount to R.drawable.love,
        shareCount to R.drawable.share
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Saved ${formatTimeAgo(save.time)}",
            style = Typography.labelMedium.copy(color = GrayTextColor)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            statistics.forEach { statistic ->
                PostStatistic(statistic.second, statistic.first)
            }
        }
    }
}

@Composable
private fun PostStatistic(@DrawableRes iconRes: Int, count: Long) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = "Icon",
            modifier = Modifier.size(16.dp),
            tint = GrayTextColor
        )

        Text(
            text = "$count",
            fontSize = 12.sp,
            color = GrayTextColor
        )
    }
}