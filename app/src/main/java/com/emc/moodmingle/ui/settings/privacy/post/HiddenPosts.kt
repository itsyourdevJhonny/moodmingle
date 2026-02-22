package com.emc.moodmingle.ui.settings.privacy.post

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.normal.HideEntityFirebase
import com.emc.moodmingle.domain.remote.model.post.normal.PostEntityFirebase
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.settings.favorites.FavoriteSkeletonCard
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.dialogFullSizeProperties
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.pagination.executePagination
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.remote.HideViewModelFirebase
import com.emc.moodmingle.viewmodel.remote.PostViewModelFirebase
import kotlinx.coroutines.flow.first
import kotlin.collections.set

@Composable
fun HiddenPosts(userEntityFirebase: UserEntityFirebase) {
    val hideViewModelFirebase = hiltViewModel<HideViewModelFirebase>()
    val hiddenPosts by hideViewModelFirebase.userHides.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userEntityFirebase) {
        hideViewModelFirebase.loadHiddenByUser(userEntityFirebase.uid)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.hidden),
                    contentDescription = "Hidden",
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawGradient()
                )

                Text(
                    text = "Hidden Posts",
                    style = Typography.bodyMedium
                )
            }

            if (hiddenPosts.isNotEmpty()) {
                Text(
                    text = "${hiddenPosts.size} ${if (hiddenPosts.size == 1) "Post" else "Posts"}",
                    style = Typography.bodyMedium.copy(color = GrayTextColor)
                )
            }
        }
    }

    if (showDialog) {
        ShowHiddenPostDialog(hiddenPosts, onShowDialog = { showDialog = it })
    }
}

@Composable
private fun ShowHiddenPostDialog(
    hiddenPosts: List<HideEntityFirebase>,
    onShowDialog: (Boolean) -> Unit
) {
    Dialog(
        onDismissRequest = { onShowDialog(false) },
        properties = dialogFullSizeProperties()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryDark)
                .padding(vertical = 48.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .clickable { onShowDialog(false) }
                )

                Text(
                    text = "Hidden Posts",
                    color = Color.White,
                )

                Text(
                    text = "(${hiddenPosts.size})",
                    color = GrayTextColor,
                    style = Typography.bodyMedium,
                )
            }

            DrawNoPaddingLine(thickness = 0.5.dp, modifier = Modifier.padding(top = 8.dp))

            DisplayHiddenPosts(hiddenPosts)
        }
    }
}

@Composable
private fun DisplayHiddenPosts(hiddenPosts: List<HideEntityFirebase>) {
    val context = LocalContext.current
    val hideViewModelFirebase = hiltViewModel<HideViewModelFirebase>()
    val postViewModelFirebase = hiltViewModel<PostViewModelFirebase>()
    val userViewModelFirebase = hiltViewModel<FirebaseUserViewModel>()

    val listState = rememberLazyListState()
    val userCache = remember { mutableStateMapOf<String, UserEntityFirebase?>() }
    val postCache = remember { mutableStateMapOf<String, PostEntityFirebase?>() }
    val pageSize = 10

    val enterAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        enterAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
        )
    }

    val sortedHiddenPosts = remember(hiddenPosts) {
        hiddenPosts.sortedByDescending { it.time }
    }

    // make loadedCount reactive to saved.size
    var loadedCount by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(sortedHiddenPosts) {
        loadedCount = sortedHiddenPosts.size.coerceAtMost(pageSize)
    }

    // visible list derived from loadedCount
    val visibleList by remember(loadedCount, sortedHiddenPosts) {
        derivedStateOf { sortedHiddenPosts.take(loadedCount) }
    }

    LaunchedEffect(visibleList) {
        visibleList.forEach { hidden ->
            if (!userCache.containsKey(hidden.userUid)) {
                val user = userViewModelFirebase.getUserByUid(hidden.userUid)
                    .first()
                    .getOrNull()
                userCache[hidden.userUid] = user
            }
            if (!postCache.containsKey(hidden.postId)) {
                val post = postViewModelFirebase.getPostById(hidden.postId)
                    .first()
                postCache[hidden.postId] = post
            }
        }
    }

    // -----------------------
    // PAGINATION TRIGGER ON SCROLL
    // -----------------------
    LaunchedEffect(listState) {
        executePagination(
            listState,
            visibleList,
            sortedHiddenPosts,
            pageSize,
            onLoadedCount = { loadedCount = it }
        )
    }

    // flag to track loading
    val firstPage = sortedHiddenPosts.take(loadedCount)
    var firstPageReady by remember { mutableStateOf(false) }

    LaunchedEffect(firstPage) {
        firstPage.forEach { hidden ->
            if (!userCache.containsKey(hidden.userUid)) {
                val user = userViewModelFirebase.getUserByUid(hidden.userUid).first().getOrNull()
                userCache[hidden.userUid] = user
            }
            if (!postCache.containsKey(hidden.postId)) {
                val post = postViewModelFirebase.getPostById(hidden.postId).first()
                postCache[hidden.postId] = post
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

    LazyColumn(
        state = listState,
        modifier = Modifier.padding(top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(visibleList, key = { it.postId + it.time }) { hiddenPost ->
            if (userCache[hiddenPost.userUid] == null && postCache[hiddenPost.postId] == null) {
                FavoriteSkeletonCard()
            } else {
                val userState: State<UserEntityFirebase?> =
                    produceState(initialValue = null, hiddenPost.userUid) {
                        // try cache first
                        value =
                            userCache[hiddenPost.userUid] ?: userViewModelFirebase.getUserByUid(
                                hiddenPost.userUid
                            ).first().getOrNull().also { userCache[hiddenPost.userUid] = it }
                    }

                // POST STATE
                val postState: State<PostEntityFirebase?> =
                    produceState(initialValue = null, hiddenPost.postId) {
                        value = postCache[hiddenPost.postId] ?: postViewModelFirebase.getPostById(
                            hiddenPost.postId
                        ).first().also { postCache[hiddenPost.postId] = it }
                    }

                val userEntity = userState.value
                val postEntity = postState.value

                Box(
                    modifier = Modifier
                        .background(SecondaryDark)
                        .graphicsLayer {
                            alpha = enterAnim.value
                            translationY = (50f * (1f - enterAnim.value))
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AsyncImage(
                                model = userEntity?.avatarUrl,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Column {
                                userEntity?.username?.let {
                                    Text(
                                        text = it,
                                        textDecoration = TextDecoration.LineThrough,
                                        style = Typography.bodyMedium.copy(color = Color.White)
                                    )
                                }

                                postEntity?.description?.let {
                                    Text(
                                        text = it,
                                        textDecoration = TextDecoration.LineThrough,
                                        style = Typography.bodySmall.copy(color = GrayTextColor),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.widthIn(max = 180.dp)
                                    )
                                }
                            }
                        }

                        TextButton(
                            onClick = {
                                hideViewModelFirebase.delete(hiddenPost)

                                Toast.makeText(
                                    context,
                                    "Post unhidden successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.background(BrushPrimaryGradient, CircleShape),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                containerColor = Color.Transparent
                            )
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.view),
                                contentDescription = "Unhide",
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                modifier = Modifier.padding(start = 4.dp),
                                text = "Unhide"
                            )
                        }
                    }
                }
            }
        }
    }
}