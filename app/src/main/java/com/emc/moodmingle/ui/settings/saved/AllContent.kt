package com.emc.moodmingle.ui.settings.saved

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.saved.SaveEntityFirebase
import com.emc.moodmingle.data.firebase.model.UserEntityFirebase
import com.emc.moodmingle.data.model.post.formatTimeAgo
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.modifier.scaleOnPress
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.firebase.PostViewModelFirebase
import com.emc.moodmingle.viewmodel.firebase.saved.SaveViewModelFirebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("FrequentlyChangingValue")
@Composable
fun AllContent(
    saved: List<SaveEntityFirebase>,
    postViewModelFirebase: PostViewModelFirebase,
    saveViewModelFirebase: SaveViewModelFirebase,
    userViewModelFirebase: FirebaseUserViewModel,
) {
    val scope = rememberCoroutineScope()

    // -----------------------
    // CACHES FOR USERS & POSTS
    // -----------------------
    val userCache = remember { mutableStateMapOf<String, UserEntityFirebase?>() }
    val postCache = remember { mutableStateMapOf<String, PostEntityFirebase?>() }

    val pageSize = 10

    // make loadedCount reactive to saved.size
    var loadedCount by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(saved) {
        loadedCount = saved.size.coerceAtMost(pageSize)
    }

    // visible list derived from loadedCount
    val visibleList by remember(loadedCount, saved) {
        derivedStateOf { saved.take(loadedCount) }
    }

    // LazyColumn state
    val listState = rememberLazyListState()

    // -----------------------
    // PRELOAD FIRST PAGE INTO CACHE
    // -----------------------
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

    // -----------------------
    // PAGINATION TRIGGER ON SCROLL
    // -----------------------
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filterNotNull()
            .collect { lastVisible ->
                val nearEnd = visibleList.lastIndex - 1
                if (lastVisible >= nearEnd && visibleList.size < saved.size) {
                    // load next batch
                    loadedCount = (visibleList.size + pageSize).coerceAtMost(saved.size)
                }
            }
    }

    // flag to track loading
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

    // -----------------------
    // 2. SHOW LOADING UNTIL FIRST PAGE READY
    // -----------------------
    if (!firstPageReady) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // -----------------------
    // LAZYCOLUMN
    // -----------------------
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryDark)
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(visibleList, key = { _, save -> save.id }) { _, save ->
            // -----------------------
            // REMOVAL ANIMATION
            // -----------------------
            val visible = rememberSaveable(save.id) { mutableStateOf(true) }

            AnimatedVisibility(
                visible = visible.value,
                enter = fadeIn(tween(350, easing = LinearOutSlowInEasing)),
                exit = fadeOut(tween(300, easing = FastOutLinearInEasing))
            ) {
                if (userCache[save.userUid] == null && postCache[save.postId] == null) {
                    SavedSkeleton()
                } else {
                    ItemContent(
                        userCache,
                        postCache,
                        save,
                        contentType = "ALL",
                        postViewModelFirebase,
                        userViewModelFirebase,
                        saveViewModelFirebase,
                        onRemove = {
                            visible.value = false
                            scope.launch {
                                delay(300)
                                saveViewModelFirebase.delete(save)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MediaCard(
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

                if (post.urls.isNotEmpty()) {
                    PostContent(post)
                } else {
                    TextItem(post)
                }

                PostStatistics(post, save)
            }
        }
    }
}

@Composable
fun BottomSheetItem(
    text: String,
    @DrawableRes iconRes: Int,
    onType: (String) -> Unit,
    onShowSheet: (Boolean) -> Unit,
    onLoading: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onType(text)
                onShowSheet(false)
                onLoading(true)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = text,
                modifier = Modifier.size(24.dp),
                tint = if (text == "Remove") Color.Red else Color.White
            )

            Row(
                modifier = Modifier.padding(end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = if (text.startsWith("Remove From")) "Remove From Collection " else text)

                if (text.startsWith("Remove From")) {
                    val collectionName =
                        text.substring("Remove From Collection".length, text.length)

                    Text(text = "(")

                    if (collectionName.isNotBlank()) {
                        Text(
                            text = text.substring("Remove From Collection".length, text.length),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 90.dp),
                            color = GrayTextColor
                        )
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(20.dp)
                                .graphicsLayer(alpha = 0.99f)
                                .drawGradient(),
                            strokeWidth = 2.dp
                        )
                    }

                    Text(text = if (collectionName.length > 6) ")" else " )")
                }
            }
        }
    }
}