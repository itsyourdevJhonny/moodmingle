package com.emc.moodmingle.ui.settings.saved.text


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.normal.PostEntityFirebase
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.domain.remote.model.saved.SaveEntityFirebase
import com.emc.moodmingle.domain.local.model.post.formatTimeAgo
import com.emc.moodmingle.ui.post.text.ExpandableAutoDetectClickableText
import com.emc.moodmingle.ui.settings.saved.ItemContent
import com.emc.moodmingle.ui.settings.saved.PostStatistics
import com.emc.moodmingle.ui.settings.saved.SavedSkeleton
import com.emc.moodmingle.ui.settings.saved.utils.EmptyComponent
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.scaleOnPress
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.remote.PostViewModelFirebase
import com.emc.moodmingle.viewmodel.remote.saved.SaveViewModelFirebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextContent(
    saved: List<SaveEntityFirebase>,
    postViewModelFirebase: PostViewModelFirebase,
    saveViewModelFirebase: SaveViewModelFirebase,
    userViewModelFirebase: FirebaseUserViewModel,
) {
    val scope = rememberCoroutineScope()

    val userCache = remember { mutableStateMapOf<String, UserEntityFirebase?>() }
    val postCache = remember { mutableStateMapOf<String, PostEntityFirebase?>() }

    val pageSize = 10

    var loadedCount by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(saved) {
        loadedCount = saved.size.coerceAtMost(pageSize)
    }

    val visibleList by remember(loadedCount, saved) {
        derivedStateOf { saved.take(loadedCount) }
    }


    val listState = rememberLazyListState()

    LaunchedEffect(visibleList) {
        visibleList.forEach { save ->
            if (!userCache.containsKey(save.userUid)) {
                val user = userViewModelFirebase.getUserByUid(save.userUid)
                    .first()
                    .getOrNull()
                userCache[save.userUid] = user
            }
            if (!postCache.containsKey(save.postId)) {
                val post = postViewModelFirebase.getPostById(save.postId)
                    .first()
                postCache[save.postId] = post
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filterNotNull()
            .collect { lastVisible ->
                val nearEnd = visibleList.lastIndex - 1
                if (lastVisible >= nearEnd && visibleList.size < saved.size) {
                    loadedCount = (visibleList.size + pageSize).coerceAtMost(saved.size)
                }
            }
    }

    val firstPage = saved.take(loadedCount)
    var firstPageReady by remember { mutableStateOf(false) }

    LaunchedEffect(firstPage) {
        firstPage.forEach { save ->
            if (!userCache.containsKey(save.userUid)) {
                val user = userViewModelFirebase.getUserByUid(save.userUid).first().getOrNull()
                userCache[save.userUid] = user
            }
            if (!postCache.containsKey(save.postId)) {
                val post = postViewModelFirebase.getPostById(save.postId).first()
                postCache[save.postId] = post
            }
        }
        firstPageReady = true
    }

    if (!firstPageReady) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (saved.isEmpty()) {
        EmptyComponent(R.drawable.empty, "You don't any Text saved post.")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryDark)
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Spacer(Modifier.height(1.dp)) }

        itemsIndexed(
            items = saved,
            key = { _, save -> save.id }
        ) { index, save ->

            var visible by rememberSaveable { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                delay(index * 60L)
                visible = true
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 350,
                        easing = LinearOutSlowInEasing
                    )
                ),
                exit = fadeOut(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutLinearInEasing
                    )
                )
            ) {
                if (userCache[save.userUid] == null && postCache[save.postId] == null) {
                    SavedSkeleton()
                } else {
                    ItemContent(
                        userCache,
                        postCache,
                        save,
                        contentType = "TEXT",
                        postViewModelFirebase,
                        userViewModelFirebase,
                        saveViewModelFirebase,
                        onRemove = {
                            visible = false
                            scope.launch {
                                delay(300)
                                saveViewModelFirebase.delete(save)
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun TextCard(
    user: UserEntityFirebase?,
    post: PostEntityFirebase?,
    save: SaveEntityFirebase,
    onShowSheet: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SecondaryDark, RoundedCornerShape(8.dp))
            .scaleOnPress()
    ) {
        if (user != null && post != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterStart),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AsyncImage(
                            model = user.avatarUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Column {
                            Text(
                                text = formatTimeAgo(post.timeAgo),
                                style = Typography.labelSmall.copy(color = GrayTextColor)
                            )
                            Text(
                                text = user.username,
                                fontSize = 13.sp
                            )
                        }
                    }

                    if (save.pinned) {
                        Icon(
                            painter = painterResource(R.drawable.pin),
                            contentDescription = "Pinned",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Red
                        )
                    }

                    Icon(
                        painter = painterResource(R.drawable.more),
                        contentDescription = "More",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable { onShowSheet(true) }
                    )
                }

                ExpandableAutoDetectClickableText(
                    fullText = post.description,
                    style = Typography.bodySmall,
                    hasPadding = true
                )

                PostStatistics(post, save)
            }
        }
    }
}