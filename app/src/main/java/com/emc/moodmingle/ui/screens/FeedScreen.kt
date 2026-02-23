package com.emc.moodmingle.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.local.model.post.user.PostType
import com.emc.moodmingle.domain.remote.model.post.normal.HideEntityFirebase
import com.emc.moodmingle.domain.remote.model.post.normal.PostEntityFirebase
import com.emc.moodmingle.domain.remote.model.post.normal.ShareEntityFirebase
import com.emc.moodmingle.domain.remote.model.post.remix.RemixEntity
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.domain.remote.viewmodel.post.remix.RemixViewModel
import com.emc.moodmingle.ui.feed.DailyMoodSection
import com.emc.moodmingle.ui.post.MultimediaCard
import com.emc.moodmingle.ui.post.PostMedia
import com.emc.moodmingle.ui.post.comment.CommentBottomSheet
import com.emc.moodmingle.ui.post.items.NormalPostItem
import com.emc.moodmingle.ui.post.skeleton.SharedPostSkeleton
import com.emc.moodmingle.ui.post.text.ExpandableAutoDetectClickableText
import com.emc.moodmingle.ui.profile.RenderMultimediaContent
import com.emc.moodmingle.ui.profile.SharedUserInformation
import com.emc.moodmingle.ui.remix.RemixItem
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.ui.users.ActiveUserList
import com.emc.moodmingle.viewmodel.remote.CombinedPost
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel
import com.emc.moodmingle.viewmodel.remote.HideViewModelFirebase
import com.emc.moodmingle.viewmodel.remote.PostViewModelFirebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun FeedScreen(
    onCreateClick: () -> Unit,
    onClick: (String) -> Unit,
    onChat: (String, String) -> Unit,
    onRemix: (String, String) -> Unit,
    onCreate: () -> Unit,
) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val postViewModel = hiltViewModel<PostViewModelFirebase>()
    val hideViewModel = hiltViewModel<HideViewModelFirebase>()
    val remixViewModel = hiltViewModel<RemixViewModel>()

    val currentUser by userViewModel.loggedUser
    val currentUserId = currentUser?.uid.orEmpty()

    val hiddenPosts by hideViewModel.userHides.collectAsState()

    var selectedMoodText by remember { mutableStateOf("All") }

    var showCommentBottomSheet by remember { mutableStateOf(false) }
    var selectedRemix by remember { mutableStateOf<RemixEntity?>(null) }

    LaunchedEffect(selectedMoodText) {
        postViewModel.getFilteredPostsByMood(selectedMoodText)
    }

    val allPosts by postViewModel.getAllCombinedPosts().collectAsState(initial = emptyList())

    val posts = if (selectedMoodText == "All") allPosts else allPosts

    val listState = rememberLazyListState()
    var pageSize by rememberSaveable { mutableIntStateOf(10) }
    var isLoadingMore by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(listState, posts, pageSize) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null &&
                    lastVisibleIndex >= pageSize - 3 &&
                    !isLoadingMore &&
                    pageSize < posts.size
                ) {
                    isLoadingMore = true
                    delay(300)
                    pageSize += 10
                    isLoadingMore = false
                }
            }
    }

    val pagedPosts = posts.take(pageSize)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item { ActiveUserList(onCreate) }

            item { CreatePostSection(onCreateClick) }

            item { DailyMoodSection() }

            items(
                items = pagedPosts,
                key = { it.id }
            ) { combinedPost ->
                when (combinedPost.type) {
                    PostType.USER_POST -> {
                        PostItem(combinedPost, hiddenPosts, userViewModel, onClick, onChat)
                    }

                    PostType.NORMAL_POST -> {
                        NormalPostItem(currentUserId, combinedPost, userViewModel, onChat)
                    }

                    PostType.SHARED_POST -> ShareItem(combinedPost, postViewModel, onChat)

                    else -> {
                        RemixItem(
                            combinedPost,
                            remixViewModel,
                            userViewModel,
                            onClick,
                            onRemix,
                            onSelectedRemix = { selectedRemix = it },
                            onShowComment = { showCommentBottomSheet = it }
                        )
                    }
                }
            }

            if (isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                        content = { CircularProgressIndicator() }
                    )
                }
            }
        }

        if (showCommentBottomSheet) {
            selectedRemix?.let { remix ->
                CommentBottomSheet(remix, onDismiss = { showCommentBottomSheet = false })
            }
        }

        FloatingRefreshButton(listState) { postViewModel.loadPosts() }
    }
}

@Composable
private fun PostItem(
    combinedPost: CombinedPost,
    hiddenPosts: List<HideEntityFirebase>,
    userViewModel: FirebaseUserViewModel,
    onClick: (String) -> Unit,
    onChatClick: (String, String) -> Unit,
) {
    val post by remember { mutableStateOf(combinedPost.entity as PostEntityFirebase) }

    if (hiddenPosts.any { it.postId == post.id }) return

    var showShareSheet by remember { mutableStateOf(false) }
    var userEntity by remember { mutableStateOf<UserEntityFirebase?>(null) }

    LaunchedEffect(post.userId) {
        userEntity = userViewModel.getUserByUid(post.userId).first().getOrNull()
    }

    if (post.urls.isNotEmpty()) {
        MultimediaCard(
            composable = {
                PostMedia(mediaUrls = post.urls, onShowShareSheet = { showShareSheet = it })
            },
            postEntity = post,
            userEntity = userEntity,
            postType = "IMAGE",
            onClick = onClick,
            showShareSheet = showShareSheet,
            onShowShareSheet = { showShareSheet = it },
            onChatClick = onChatClick
        )
    } else {
        MultimediaCard(
            composable = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    ExpandableAutoDetectClickableText(
                        fullText = post.description,
                        style = MaterialTheme.typography.bodyLarge,
                        hasPadding = false
                    )
                }
            },
            postEntity = post,
            userEntity = userEntity,
            postType = "TEXT",
            onClick = onClick,
            showShareSheet = showShareSheet,
            onShowShareSheet = { showShareSheet = it },
            onChatClick = onChatClick
        )
    }
}

@Composable
private fun ShareItem(
    combinedPost: CombinedPost,
    postViewModel: PostViewModelFirebase,
    onChatClick: (String, String) -> Unit,
) {
    val shareEntity = combinedPost.entity as ShareEntityFirebase
    val postId = shareEntity.postId
    val context = LocalContext.current

    var postEntity by remember { mutableStateOf<PostEntityFirebase?>(null) }

    LaunchedEffect(postId) {
        postEntity = try {
            postViewModel.getPostByIdOnce(postId)
        } catch (_: Exception) {
            Toast.makeText(context, "Failed to load post", Toast.LENGTH_SHORT).show()
            null
        }
    }

    Column(
        modifier = Modifier
            .padding(top = 2.dp)
            .fillMaxWidth()
            .background(SecondaryDark),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            postEntity?.let { SharedUserInformation(shareEntity) }
        }

        if (postEntity == null) {
            SharedPostSkeleton()
        } else {
            RenderMultimediaContent(postEntity!!, onChatClick)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FloatingRefreshButton(listState: LazyListState, onRefresh: suspend () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    val isVisible by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            offsetX = 0f
            offsetY = 0f
        }
    }

    var rotate by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (rotate) 360f else 0f,
        animationSpec = tween(600)
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300)
    )

    val scale by animateFloatAsState(
        targetValue = if (listState.isScrollInProgress) 0.85f else 1f,
        animationSpec = tween(250)
    )

    val bounceOffsetY by animateFloatAsState(
        targetValue = offsetY,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    if (alpha <= 0f) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), bounceOffsetY.roundToInt()) }
                .graphicsLayer(alpha = alpha, rotationZ = rotation, scaleX = scale, scaleY = scale)
                .size(60.dp)
                .clip(CircleShape)
                .background(BrushPrimaryGradient)
                .combinedClickable(
                    onClick = {
                        rotate = true
                        scope.launch {
//                            scrollState.animateScrollTo(0)
                            listState.animateScrollToItem(0)
                            delay(200)
                            onRefresh()
                            rotate = false
                        }
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                80,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )
                    },
                    onDoubleClick = {
                        scope.launch { /*scrollState.animateScrollTo(0)*/
                            listState.animateScrollToItem(0)
                        }
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                )
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(id = R.drawable.refresh),
                contentDescription = "REFRESH",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun CreatePostSection(onCreateClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(BrushPrimaryGradient, RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        onClick = onCreateClick
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = Color.White
        )
        Text(text = "Share what you feel", color = Color.White)
    }
}

@Composable
fun LoadingMorePosts() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        text = "Loading more post...",
        style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
        textAlign = TextAlign.Center,
        color = Color.Gray
    )
}